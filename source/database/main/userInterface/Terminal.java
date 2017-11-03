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

import com.sun.glass.events.KeyEvent;

import database.main.UserCancelException;
import database.services.ServiceRegistry;
import database.services.pluginRegistry.IPluginRegistry;
import database.services.stringComplete.IStringComplete;
import database.services.stringUtility.StringUtility;

public class Terminal implements ITerminal {
	private Map<OutputInformation, String> collectedLines;
	private GraphicalUserInterface graphicalUserInterface;
	private StringUtility stringUtility;

	public Terminal(GraphicalUserInterface graphicalUserInterface) {
		this.graphicalUserInterface = graphicalUserInterface;
		collectedLines = new LinkedHashMap<>();
		stringUtility = new StringUtility();
	}

	@Override
	public void blockInput() {
		graphicalUserInterface.blockInput();
	}

	@Override
	public int checkRequest(Collection<String> collection, int initialPosition, String request)
			throws InterruptedException, BadLocationException {
		StringUtility su = new StringUtility();
		int nextSelection = initialPosition;
		int lastKey = 0;
		if (collection.isEmpty()) {
			printLine(request, OutputType.CLEAR, StringFormat.ITALIC);
			newLine(OutputType.ADD);
			printLine("no entries", OutputType.ADD, StringFormat.STANDARD);
			waitForInput();
			return -1;
		}
		blockInput();
		while (lastKey != KeyEvent.VK_ENTER) {
			printLine(request, OutputType.CLEAR, StringFormat.ITALIC);
			newLine(OutputType.ADD);
			printLine(su.formatCheckLine(collection, nextSelection), OutputType.ADD, StringFormat.STANDARD);
			graphicalUserInterface.waitForKeyInput();
			lastKey = graphicalUserInterface.getLastKeyInput();
			if (lastKey == KeyEvent.VK_DOWN) {
				if (!(nextSelection == collection.size() - 1)) {
					nextSelection++;
				}
			}
			else if (lastKey == KeyEvent.VK_UP) {
				if (!(nextSelection == -1)) {
					nextSelection--;
				}
			}
			else if (lastKey != KeyEvent.VK_ENTER) {
				if (su.findString(-1, (char) lastKey, collection) == -1) {
					nextSelection = -1;
				}
				else {
					int indexOfNextString = su.findString(nextSelection, (char) lastKey, collection);
					nextSelection = indexOfNextString == nextSelection ? su.findString(0, (char) lastKey, collection)
							: indexOfNextString;
				}
			}
		}
		return nextSelection >= -1 ? nextSelection : -1;
	}

	@Override
	public void collectLine(Object output, StringFormat stringFormat, String headline) {
		collectedLines.put(new OutputInformation(output, OutputType.ADD, stringFormat), headline);
	}

	@Override
	public void errorMessage() throws BadLocationException, InterruptedException {
		graphicalUserInterface.printLine("invalid input", OutputType.CLEAR, StringFormat.ITALIC);
		waitForInput();
	}

	@Override
	public void getLineOfCharacters(char character, OutputType stringType) throws BadLocationException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < graphicalUserInterface.getNumberOfCharsPerLine(character); i++) {
			builder.append(character);
		}
		printLine(builder.toString(), stringType, StringFormat.STANDARD);
	}

	@Override
	public void printCollectedLines() throws InterruptedException, BadLocationException {
		if (!collectedLines.isEmpty()) {
			List<String> list = new ArrayList<>();
			for (String string : collectedLines.values()) {
				if (!list.contains(string)) {
					list.add(string);
				}
			}
			for (String string : list) {
				graphicalUserInterface.printLine(string, OutputType.ADD, StringFormat.BOLD);
				for (Entry<OutputInformation, String> entry : collectedLines.entrySet()) {
					if (entry.getValue().equals(string)) {
						graphicalUserInterface.printLine(entry.getKey().getOutput(), entry.getKey().getStringType(),
								entry.getKey().getStringFormat());
					}
				}
			}
			waitForInput();
			collectedLines.clear();
		}
	}

	@Override
	public void printLine(Object object, OutputType stringType, StringFormat stringFormat) throws BadLocationException {
		graphicalUserInterface.printLine(object, stringType, stringFormat);
	}

	@Override
	public String request(String printOut, String regex, String inputText, IStringComplete stringComplete,
			int levenshteinDistance)
			throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		boolean request = true;
		String result = null;
		String input = null;
		String[] splitResult = regex.substring(1, regex.length() - 1).split("\\|");
		if (!inputText.isEmpty()) {
			graphicalUserInterface.setInputText(inputText);
			graphicalUserInterface.selectInputText(0, inputText.length());
		}
		else {
			graphicalUserInterface.setInputText("");
		}
		while (request) {
			printLine(printOut + ":", OutputType.CLEAR, StringFormat.ITALIC);
			if (stringComplete != null) {
				completeString(stringComplete);
			}
			else {
				while (!isNextKeyEnterOrEscape());
			}
			input = graphicalUserInterface.getInputText();
			graphicalUserInterface.setInputText("");
			if (input.equalsIgnoreCase("back")) {
				throw new UserCancelException();
			}
			else if (input.equalsIgnoreCase("help")) {
				newLine(OutputType.ADD);
				printLine(regex, OutputType.ADD, StringFormat.STANDARD);
				waitForInput();
			}
			else if (input.matches(regex)) {
				result = input;
				request = false;
			}
			else if ("DATE".equals(regex)
					&& (stringUtility.testDateString(stringUtility.parseDateString(input)) || input.isEmpty())) {
				result = stringUtility.parseDateString(input);
				request = false;
			}
			else if ("TIME".equals(regex)
					&& (stringUtility.testTimeString(stringUtility.parseTimeString(input)) || input.isEmpty())) {
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

	@Override
	public void update() throws BadLocationException, InterruptedException, SQLException {
		graphicalUserInterface.clearOutput();
		graphicalUserInterface.blockInput();
		ServiceRegistry.Instance().get(IPluginRegistry.class).initialOutput();
		graphicalUserInterface.releaseInput();
		printCollectedLines();
	}

	@Override
	public void waitForInput() throws InterruptedException {
		graphicalUserInterface.releaseInput();
		graphicalUserInterface.setInputColor(Color.BLACK);
		do {
			graphicalUserInterface.waitForKeyInput();
		}
		while ((graphicalUserInterface.getLastKeyInput() == KeyEvent.VK_DOWN
				|| graphicalUserInterface.getLastKeyInput() == KeyEvent.VK_UP)
				&& graphicalUserInterface.isScrollable());
		graphicalUserInterface.setInputColor(Color.WHITE);
	}

	private void completeString(IStringComplete stringComplete)
			throws InterruptedException, SQLException, UserCancelException {
		String inputString = "";
		String selection;
		int inputCaretPosition;
		graphicalUserInterface.setRemoveSelectionAndLastKey(true);
		do {
			graphicalUserInterface.blockInput();
			inputString = graphicalUserInterface.getInputText();
			inputCaretPosition = graphicalUserInterface.getInputCaretPosition();
			selection = stringComplete.getMostUsedString(inputString, "");
			if (inputString.isEmpty() || !graphicalUserInterface.getSelectedText().equals(inputString)
					|| !selection.isEmpty()) {
				graphicalUserInterface.setInputText(inputString + selection);
				graphicalUserInterface.selectInputText(inputString.length(), (inputString + selection).length());
				if (selection.isEmpty() && graphicalUserInterface.getInputText().length() > inputCaretPosition + 1) {
					graphicalUserInterface.setInputCaretPosition(inputCaretPosition + 1);
				}
			}
			graphicalUserInterface.releaseInput();
		}
		while (!isNextKeyEnterOrEscape());
		graphicalUserInterface.setRemoveSelectionAndLastKey(false);
	}

	private boolean isNextKeyEnterOrEscape() throws InterruptedException, UserCancelException {
		graphicalUserInterface.waitForDocumentInput();
		if (graphicalUserInterface.getLastKeyInput() == KeyEvent.VK_ESCAPE) {
			throw new UserCancelException();
		}
		else if (graphicalUserInterface.getLastKeyInput() == KeyEvent.VK_ENTER) {
			return true;
		}
		else {
			return false;
		}
	}
}
