package database.main.userInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.date.Date;
import database.main.date.Time;
import database.main.userInterface.autocompletion.Autocompletion;
import database.plugin.Plugin;

public class Terminal {
	private static List<OutputInformation>	collectedLines;
	private static GraphicalUserInterface	graphicalUserInterface;
	private static PluginContainer			pluginContainer;

	public Terminal(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer) {
		Terminal.graphicalUserInterface = graphicalUserInterface;
		Terminal.pluginContainer = pluginContainer;
		Terminal.collectedLines = new ArrayList<OutputInformation>();
	}

	public static void blockInput() {
		graphicalUserInterface.blockInput();
	}

	public static int checkRequest(Collection<String> collection) throws InterruptedException, BadLocationException {
		return graphicalUserInterface.checkRequest(collection);
	}

	public static void collectLine(Object output, StringFormat stringFormat) {
		collectedLines.add(new OutputInformation(output, StringType.SOLUTION, stringFormat));
	}

	public static void errorMessage() throws BadLocationException, InterruptedException {
		printLine("invalid input", StringType.REQUEST, StringFormat.ITALIC);
		waitForInput();
	}

	public static List<OutputInformation> getCollectedLines() {
		return collectedLines;
	}

	public static int getLastKey() {
		return graphicalUserInterface.getLastKey();
	}

	public static void getLineOfCharacters(char character) throws BadLocationException {
		graphicalUserInterface.getLineOfCharacters(character);
	}

	public static void initialOutput() throws BadLocationException {
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin.getDisplay()) {
				plugin.initialOutput();
			}
		}
	}

	public static void printCollectedLines() throws InterruptedException, BadLocationException {
		if (!collectedLines.isEmpty()) {
			Terminal.getLineOfCharacters('-');
			for (OutputInformation output : collectedLines) {
				graphicalUserInterface.printLine(output);
			}
			waitForInput();
		}
	}

	public static synchronized void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		graphicalUserInterface.printLine(object, stringType, stringFormat);
	}

	public static String readKey() throws InterruptedException {
		return graphicalUserInterface.readKey();
	}

	public static String readLine() throws InterruptedException {
		return graphicalUserInterface.readLine();
	}

	public static void releaseInput() {
		graphicalUserInterface.releaseInput();
	}

	public static String request(String printOut, String regex) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", null);
	}

	public static String request(String printOut, String regex, Autocompletion autocompletion) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", autocompletion);
	}

	public static String request(String printOut, String regex, String inputText) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, null);
	}

	public static String request(String printOut, String regex, String inputText, Autocompletion autocompletion) throws InterruptedException, BadLocationException {
		boolean request = true;
		String result = null;
		String input = null;
		if (!inputText.isEmpty()) {
			setInputText(inputText);
		}
		while (request) {
			printLine(printOut + ":", StringType.REQUEST, StringFormat.ITALIC);
			input = autocompletion != null ? autocompletion.getLine() : readLine();
			graphicalUserInterface.clearInput();
			if (input.equals("back")) {
				throw new CancellationException();
			}
			else if (input.equals("help")) {
				printLine(regex, StringType.SOLUTION, StringFormat.STANDARD);
				waitForInput();
			}
			else if (input.matches(regex) || regex == "DATE" && Date.testDateString(input) || regex == "TIME" && Time.testTimeString(input)) {
				result = input;
				request = false;
			}
			else {
				errorMessage();
			}
		}
		return result;
	}

	public static void resetLastKey() {
		graphicalUserInterface.resetLastKey();
	}

	public static void setInputText(String string) {
		graphicalUserInterface.setInputText(string);
	}

	public static void showMessageDialog(Throwable e) {
		graphicalUserInterface.showMessageDialog(e);
	}

	public static void update() throws BadLocationException {
		graphicalUserInterface.update();
	}

	public static void waitForInput() throws InterruptedException {
		graphicalUserInterface.waitForInput();
	}
}
