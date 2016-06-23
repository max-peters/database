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

public class Terminal {
	private static Map<OutputInformation, String>	collectedLines;
	private static GraphicalUserInterface			graphicalUserInterface;
	private static PluginContainer					pluginContainer;
	private static StringUtility					stringUtility;

	public Terminal(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer) {
		Terminal.graphicalUserInterface = graphicalUserInterface;
		Terminal.pluginContainer = pluginContainer;
		Terminal.collectedLines = new LinkedHashMap<OutputInformation, String>();
		Terminal.stringUtility = new StringUtility();
	}

	public static void blockInput() {
		graphicalUserInterface.blockInput();
	}

	public static int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException {
		return graphicalUserInterface.checkRequest(collection);
	}

	public static void collectLine(Object output, StringFormat stringFormat, String headline) {
		collectedLines.put(new OutputInformation(output, StringType.SOLUTION, stringFormat), headline);
	}

	public static void errorMessage() throws BadLocationException, InterruptedException {
		Terminal.printLine("invalid input", StringType.REQUEST, StringFormat.ITALIC);
		Terminal.waitForInput();
	}

	public static void getLineOfCharacters(char character, StringType stringType) throws BadLocationException {
		graphicalUserInterface.getLineOfCharacters(character, stringType);
	}

	public static void printCollectedLines() throws InterruptedException, BadLocationException {
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

	public static synchronized void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		graphicalUserInterface.printLine(object, stringType, stringFormat);
	}

	public static String request(String printOut, String regex) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null, 0);
	}

	public static String request(String printOut, String regex, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", completeable, 0);
	}

	public static String request(String printOut, String regex, int levenshteinDistance) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null, levenshteinDistance);
	}

	public static String request(String printOut, String regex, String inputText) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, null, 0);
	}

	public static String request(String printOut, String regex, String inputText, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, completeable, 0);
	}

	public static String request(String printOut, String regex, String inputText, Completeable completeable, int levenshteinDistance)	throws InterruptedException,
																																		BadLocationException {
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

	public static void update() throws BadLocationException, InterruptedException {
		graphicalUserInterface.update();
		blockInput();
		pluginContainer.initialOutput();
		graphicalUserInterface.releaseInput();
		printCollectedLines();
	}

	public static void waitForInput() throws InterruptedException {
		graphicalUserInterface.waitForInput();
	}
}
