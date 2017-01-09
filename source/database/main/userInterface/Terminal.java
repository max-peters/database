package database.main.userInterface;

import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.UserCancelException;
import database.main.autocompletition.IAutocomplete;

public class Terminal implements ITerminal {
	private Map<OutputInformation, String>	collectedLines;
	private GraphicalUserInterface			graphicalUserInterface;
	private StringUtility					stringUtility;

	public Terminal(GraphicalUserInterface graphicalUserInterface) {
		this.graphicalUserInterface = graphicalUserInterface;
		collectedLines = new LinkedHashMap<>();
		stringUtility = new StringUtility();
	}

	@Override public void blockInput() {
		graphicalUserInterface.blockInput();
	}

	@Override public int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException {
		StringUtility su = new StringUtility();
		int position = -1;
		int current = 0;
		int lastKey = 0;
		if (collection.isEmpty()) {
			printLine("check:", StringType.REQUEST, StringFormat.ITALIC);
			printLine("no entries", StringType.SOLUTION, StringFormat.STANDARD);
			waitForInput();
			return position;
		}
		blockInput();
		while (lastKey != 10) {
			printLine("check:", StringType.REQUEST, StringFormat.ITALIC);
			printLine(su.formatCheckLine(collection, current), StringType.SOLUTION, StringFormat.STANDARD);
			graphicalUserInterface.waitForKeyInput();
			lastKey = graphicalUserInterface.getLastKeyInput();
			if (lastKey == 40) {
				if (!(current == collection.size())) {
					current++;
				}
			}
			else if (lastKey == 38) {
				if (!(current == 0)) {
					current--;
				}
			}
			else if (lastKey != 10) {
				int temp = su.findString(current, (char) lastKey, collection);
				current = temp == current ? su.findString(0, (char) lastKey, collection) : temp;
			}
		}
		if (current != 0) {
			position = current - 1;
		}
		graphicalUserInterface.releaseInput();
		return position;
	}

	@Override public void collectLine(Object output, StringFormat stringFormat, String headline) {
		collectedLines.put(new OutputInformation(output, StringType.SOLUTION, stringFormat), headline);
	}

	@Override public void errorMessage() throws BadLocationException, InterruptedException {
		graphicalUserInterface.printLine("invalid input", StringType.REQUEST, StringFormat.ITALIC);
		waitForInput();
	}

	@Override public void getLineOfCharacters(char character, StringType stringType) throws BadLocationException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < graphicalUserInterface.getNumberOfCharsPerLine(character); i++) {
			builder.append(character);
		}
		printLine(builder.toString(), stringType, StringFormat.STANDARD);
	}

	@Override public void printCollectedLines() throws InterruptedException, BadLocationException {
		if (!collectedLines.isEmpty()) {
			getLineOfCharacters('-', StringType.SOLUTION);
			List<String> list = new ArrayList<>();
			for (String string : collectedLines.values()) {
				if (!list.contains(string)) {
					list.add(string);
				}
			}
			for (String string : list) {
				graphicalUserInterface.printLine(string, StringType.SOLUTION, StringFormat.BOLD);
				for (Entry<OutputInformation, String> entry : collectedLines.entrySet()) {
					if (entry.getValue().equals(string)) {
						graphicalUserInterface.printLine(entry.getKey().getOutput(), entry.getKey().getStringType(), entry.getKey().getStringFormat());
					}
				}
			}
			waitForInput();
			collectedLines.clear();
		}
	}

	@Override public void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		graphicalUserInterface.printLine(object, stringType, stringFormat);
	}

	@Override public String request(String printOut, String regex, String inputText, IAutocomplete autocomplete,
									int levenshteinDistance) throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		boolean request = true;
		String result = null;
		String input = null;
		String[] splitResult = regex.substring(1, regex.length() - 1).split("\\|");
		if (!inputText.isEmpty()) {
			graphicalUserInterface.setInputText(inputText);
			graphicalUserInterface.selectInputText(0, inputText.length());
		}
		while (request) {
			printLine(printOut + ":", StringType.REQUEST, StringFormat.ITALIC);
			input = autocomplete != null ? autocomplete(autocomplete) : readLine();
			graphicalUserInterface.setInputText("");
			if (input.equalsIgnoreCase("back")) {
				throw new UserCancelException();
			}
			else if (input.equalsIgnoreCase("help")) {
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

	@Override public void update(PluginContainer pluginContainer) throws BadLocationException, InterruptedException {
		graphicalUserInterface.clearOutput();
		graphicalUserInterface.blockInput();
		pluginContainer.initialOutput(this);
		graphicalUserInterface.releaseInput();
		printCollectedLines();
	}

	@Override public void waitForInput() throws InterruptedException {
		graphicalUserInterface.releaseInput();
		graphicalUserInterface.setInputCaretColor(Color.BLACK);
		do {
			graphicalUserInterface.waitForKeyInput();
		}
		while ((graphicalUserInterface.getLastKeyInput() == 38 || graphicalUserInterface.getLastKeyInput() == 40) && graphicalUserInterface.isScrollable());
		graphicalUserInterface.setInputCaretColor(Color.WHITE);
	}

	private String autocomplete(IAutocomplete autocomplete) throws InterruptedException, SQLException {
		String inputString = "";
		String selection;
		int inputCaretPosition;
		graphicalUserInterface.addKeyListenerForAutocompletition();
		while (true) {
			inputString = graphicalUserInterface.getInputText();
			inputCaretPosition = graphicalUserInterface.getInputCaretPosition();
			selection = autocomplete.getMostUsedString(inputString, "");
			if (inputString.isEmpty() || !graphicalUserInterface.getSelectedText().equals(inputString) || !selection.isEmpty()) {
				graphicalUserInterface.setInputText(inputString + selection);
				graphicalUserInterface.selectInputText(inputString.length(), (inputString + selection).length());
				if (selection.isEmpty() && graphicalUserInterface.getInputText().length() > 1) {
					graphicalUserInterface.setInputCaretPosition(inputCaretPosition + 1);
				}
			}
			graphicalUserInterface.waitForDocumentInput();
			if (graphicalUserInterface.getLastKeyInput() == 10) {
				inputString = graphicalUserInterface.getInputText();
				break;
			}
		}
		graphicalUserInterface.removeKeyListenerForAutocompletition();
		return inputString;
	}

	private String readLine() throws InterruptedException {
		int lastKey = 0;
		while (lastKey != 10) {
			graphicalUserInterface.waitForKeyInput();
			lastKey = graphicalUserInterface.getLastKeyInput();
		}
		return graphicalUserInterface.getInputText();
	}
}
