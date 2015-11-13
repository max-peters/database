package database.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import database.main.date.Date;
import database.plugin.Plugin;

public class Terminal {
	private static GraphicalUserInterface	graphicalUserInterface;
	private static PluginContainer			pluginContainer;
	private static List<String>				list	= new ArrayList<String>();

	public Terminal(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer) {
		Terminal.graphicalUserInterface = graphicalUserInterface;
		Terminal.pluginContainer = pluginContainer;
	}

	public static String readLine() throws InterruptedException {
		return graphicalUserInterface.readLine();
	}

	public static void printLineMain(Object output) {
		graphicalUserInterface.printLine(buildList(output));
	}

	public static void printRequest(Object output) {
		graphicalUserInterface.printRequest(buildList(output));
	}

	public static void printLine(Object output) {
		graphicalUserInterface.printSolution(buildList(output));
	}

	public static void collectLines(Object output) {
		list.addAll(buildList(output));
	}

	public static void printCollectedLines() throws InterruptedException {
		graphicalUserInterface.printRequest(list);
		if (!list.isEmpty()) {
			graphicalUserInterface.waitForInput();
		}
	}

	public static void waitForInput() throws InterruptedException {
		graphicalUserInterface.waitForInput();
	}

	public static void blockInput() {
		graphicalUserInterface.blockInput();
	}

	public static void releaseInput() {
		graphicalUserInterface.releaseInput();
	}

	public static int checkRequest(ArrayList<String> strings) throws InterruptedException {
		return graphicalUserInterface.checkRequest(strings);
	}

	public static String request(String printOut, String regex) throws InterruptedException {
		boolean request = true;
		String result = null;
		String input = null;
		while (request) {
			Terminal.printRequest(printOut + ":");
			input = Terminal.readLine();
			if (input.equals("back")) {
				throw new CancellationException();
			}
			else if ((regex != null) && (input.matches(regex))) {
				result = input;
				request = false;
			}
			else if ((regex == null) && (Date.testDateString(input))) {
				result = input;
				request = false;
			}
			else {
				errorMessage();
			}
		}
		return result;
	}

	public static void update() {
		graphicalUserInterface.clear();
		Terminal.blockInput();
		Terminal.initialOutput();
		Terminal.releaseInput();
	}

	public static void errorMessage() throws InterruptedException {
		Terminal.printRequest("invalid input");
		graphicalUserInterface.waitForInput();
	}

	public static void initialOutput() {
		String longestString = "";
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin.getDisplay() && plugin.initialOutput() != null) {
				Terminal.printLineMain(plugin.initialOutput());
				String splitResult[] = plugin.initialOutput().split("\r\n");
				for (int i = 0; i < splitResult.length; i++) {
					if (splitResult[i].length() > longestString.length()) {
						longestString = splitResult[i];
					}
				}
			}
		}
		graphicalUserInterface.setBounds(longestString);;
	}

	private static List<String> buildList(Object object) {
		String[] splitResult = new String[0];
		if (object != null && object.toString().length() != 0) {
			splitResult = object.toString().split("\r\n");
		}
		return Arrays.asList(splitResult);
	}
}
