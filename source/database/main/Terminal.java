package database.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import database.main.GraphicalUserInterface.GraphicalUserInterface;
import database.main.GraphicalUserInterface.StringFormat;
import database.main.GraphicalUserInterface.StringType;
import database.main.date.Date;
import database.plugin.Plugin;

public class Terminal {
	private static GraphicalUserInterface	graphicalUserInterface;
	private static PluginContainer			pluginContainer;
	private static List<String>				list	= new ArrayList<String>();

	public static void blockInput() {
		graphicalUserInterface.blockInput();
	}

	public static int checkRequest(ArrayList<String> strings) throws InterruptedException, BadLocationException {
		return graphicalUserInterface.checkRequest(strings);
	}

	public static void collectLines(Object output) {
		list.add(output.toString());
	}

	public static void errorMessage() throws InterruptedException, BadLocationException {
		Terminal.printLine("invalid input", StringType.REQUEST, StringFormat.ITALIC);
		graphicalUserInterface.waitForInput();
	}

	public static void initialOutput() throws BadLocationException {
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin.getDisplay()) {
				plugin.initialOutput();
			}
		}
	}

	public static void printCollectedLines() throws InterruptedException, BadLocationException {
		for (String string : list) {
			printLine(string, StringType.REQUEST, StringFormat.STANDARD);
		}
		if (!list.isEmpty()) {
			graphicalUserInterface.waitForInput();
		}
	}

	public static void printLine(Object object, StringType stringType, StringFormat stringFormat) throws BadLocationException {
		if (object != null) {
			String output = object.toString();
			if (output.length() != 0) {
				if (!output.endsWith(System.getProperty("line.separator"))) {
					output += System.getProperty("line.separator");
				}
				if (stringType.equals(StringType.MAIN)) {
					String[] array = output.split(System.getProperty("line.separator"));
					for (int i = 0; i < array.length; i++) {
						graphicalUserInterface.setBounds(array[i]);
					}
				}
				graphicalUserInterface.printLine(output, stringType, stringFormat);
			}
		}
	}

	public static String readLine() throws InterruptedException {
		return graphicalUserInterface.readLine();
	}

	public static void releaseInput() {
		graphicalUserInterface.releaseInput();
	}

	public static String request(String printOut, String regex) throws InterruptedException, BadLocationException, CancellationException {
		boolean request = true;
		String result = null;
		String input = null;
		while (request) {
			Terminal.printLine(printOut + ":", StringType.REQUEST, StringFormat.ITALIC);
			input = Terminal.readLine();
			if (input.equals("back")) {
				throw new CancellationException();
			}
			else if (regex != null && input.matches(regex)) {
				result = input;
				request = false;
			}
			else if (regex == null && Date.testDateString(input)) {
				result = input;
				request = false;
			}
			else {
				errorMessage();
			}
		}
		return result;
	}

	public static void update() throws BadLocationException {
		graphicalUserInterface.clear();
		Terminal.blockInput();
		Terminal.initialOutput();
		Terminal.releaseInput();
	}

	public static void waitForInput() throws InterruptedException {
		graphicalUserInterface.waitForInput();
	}

	public Terminal(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer) {
		Terminal.graphicalUserInterface = graphicalUserInterface;
		Terminal.pluginContainer = pluginContainer;
	}
}
