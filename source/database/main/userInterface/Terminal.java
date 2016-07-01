package database.main.userInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.plugin.FormatterProvider;

public class Terminal {
	private Map<OutputInformation, String>	collectedLines;
	private GraphicalUserInterface			graphicalUserInterface;
	private StringUtility					stringUtility;

	public Terminal(GraphicalUserInterface graphicalUserInterface) {
		this.graphicalUserInterface = graphicalUserInterface;
		collectedLines = new LinkedHashMap<OutputInformation, String>();
		stringUtility = new StringUtility();
	}

	public void blockInput() {
		graphicalUserInterface.blockInput();
	}

	public int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException {
		return graphicalUserInterface.checkRequest(collection);
	}

	public void collectLine(Object output, StringFormat stringFormat, String headline) {
		collectedLines.put(new OutputInformation(output, StringType.SOLUTION, stringFormat), headline);
	}

	public void errorMessage() throws BadLocationException, InterruptedException {
		graphicalUserInterface.printLine("invalid input", StringType.REQUEST, StringFormat.ITALIC);
		graphicalUserInterface.waitForInput();
	}

	public void getLineOfCharacters(char character, StringType stringType) throws BadLocationException {
		graphicalUserInterface.getLineOfCharacters(character, stringType);
	}

	public void printCollectedLines() throws InterruptedException, BadLocationException {
		if (!collectedLines.isEmpty()) {
			getLineOfCharacters('-', StringType.SOLUTION);
			List<String> list = new ArrayList<String>();
			for (String string : collectedLines.values()) {
				if (!list.contains(string)) {
					list.add(string);
				}
			}
			for (String string : list) {
				graphicalUserInterface.printLine(new OutputInformation(string, StringType.SOLUTION, StringFormat.BOLD));
				for (Entry<OutputInformation, String> entry : collectedLines.entrySet()) {
					if (entry.getValue().equals(string)) {
						graphicalUserInterface.printLine(entry.getKey());
					}
				}
			}
			waitForInput();
			collectedLines.clear();
		}
	}

	public synchronized void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		graphicalUserInterface.printLine(object, stringType, stringFormat);
	}

	public String request(String printOut, String regex) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null, 0);
	}

	public String request(String printOut, String regex, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", completeable, 0);
	}

	public String request(String printOut, String regex, int levenshteinDistance) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null, levenshteinDistance);
	}

	public String request(String printOut, String regex, String inputText) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, null, 0);
	}

	public String request(String printOut, String regex, String inputText, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, completeable, 0);
	}

	public String request(String printOut, String regex, String inputText, Completeable completeable, int levenshteinDistance) throws InterruptedException, BadLocationException {
		boolean request = true;
		String result = null;
		String input = null;
		String[] splitResult = regex.substring(1, regex.length() - 1).split("\\|");
		if (!inputText.isEmpty()) {
			graphicalUserInterface.setInputText(inputText);
		}
		while (request) {
			printLine(printOut + ":", StringType.REQUEST, StringFormat.ITALIC);
			input = completeable != null ? graphicalUserInterface.autocomplete(completeable) : graphicalUserInterface.readLine();
			graphicalUserInterface.clearInput();
			if (input.equals("back")) {
				throw new CancellationException();
			}
			else if (input.equals("help")) {
				printLine(regex, StringType.SOLUTION, StringFormat.STANDARD);
				waitForInput();
			}
			else if (input.matches(regex)) {
				result = input;
				request = false;
			}
			else if ("DATE".equals(regex) && (stringUtility.testDateString(stringUtility.parseDateString(input)) || input.isEmpty())) {
				result = stringUtility.parseDateString(input);
				request = false;
			}
			else if ("TIME".equals(regex) && (stringUtility.testTimeString(stringUtility.parseTimeString(input)) || input.isEmpty())) {
				result = stringUtility.parseTimeString(input);
				request = false;
			}
			else {
				result = stringUtility.getElementWithDistance(input, splitResult, levenshteinDistance);
				if (result != null) {
					request = false;
				}
				else {
					errorMessage();
				}
			}
		}
		return result;
	}

	public void update(PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		graphicalUserInterface.update();
		graphicalUserInterface.blockInput();
		pluginContainer.initialOutput(this, formatterProvider);
		graphicalUserInterface.releaseInput();
		printCollectedLines();
	}

	public void waitForInput() throws InterruptedException {
		graphicalUserInterface.waitForInput();
	}
}
