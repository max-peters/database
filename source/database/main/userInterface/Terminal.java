package database.main.userInterface;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
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

	public Terminal(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer) {
		Terminal.graphicalUserInterface = graphicalUserInterface;
		Terminal.pluginContainer = pluginContainer;
		Terminal.collectedLines = new LinkedHashMap<OutputInformation, String>();
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
		return request(printOut, regex, "", null);
	}

	public static String request(String printOut, String regex, Completeable completeable) throws InterruptedException, BadLocationException {
		return request(printOut, regex, "", completeable);
	}

	public static String request(String printOut, String regex, int levenshteinDistance) throws InterruptedException, BadLocationException {
		boolean request = true;
		String result = null;
		String input = null;
		String[] splitResult = regex.substring(1, regex.length() - 1).split("\\|");
		while (request) {
			printLine(printOut + ":", StringType.REQUEST, StringFormat.ITALIC);
			input = graphicalUserInterface.readLine();
			graphicalUserInterface.clearInput();
			if (input.equals("back")) {
				throw new CancellationException();
			}
			else if (input.equals("help")) {
				printLine(Arrays.asList(splitResult), StringType.SOLUTION, StringFormat.STANDARD);
				waitForInput();
			}
			else {
				result = getElementWithDistance(input, splitResult, levenshteinDistance);
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

	public static String request(String printOut, String regex, String inputText) throws InterruptedException, BadLocationException {
		return request(printOut, regex, inputText, null);
	}

	public static String request(String printOut, String regex, String inputText, Completeable completeable) throws InterruptedException, BadLocationException {
		boolean request = true;
		String result = null;
		String input = null;
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
			else if ("DATE".equals(regex) && (testDateString(parseDateString(input)) || input.isEmpty())) {
				result = parseDateString(input);
				request = false;
			}
			else if ("TIME".equals(regex) && (testTimeString(parseTimeString(input)) || input.isEmpty())) {
				result = parseTimeString(input);
				request = false;
			}
			else {
				errorMessage();
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

	private static String add(String string, int k) {
		String s = string;
		for (string.length(); s.length() < k;) {
			s = "0" + s;
		}
		return s;
	}

	private static String getElementWithDistance(String input, String[] splitResult, int levenshteinDistance) {
		for (String string : splitResult) {
			if (levenshteinDistance(input, string) <= levenshteinDistance) {
				return string;
			}
		}
		return null;
	}

	private static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
		int len0 = lhs.length() + 1;
		int len1 = rhs.length() + 1;
		int[] cost = new int[len0];
		int[] newcost = new int[len0];
		for (int i = 0; i < len0; i++) {
			cost[i] = i;
		}
		for (int j = 1; j < len1; j++) {
			newcost[0] = j;
			for (int i = 1; i < len0; i++) {
				int match = lhs.charAt(i - 1) == rhs.charAt(j - 1) ? 0 : 1;
				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}
			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}
		return cost[len0 - 1];
	}

	private static String parseDateString(String date) {
		String formattedDate = null;
		if (date.isEmpty()) {
			formattedDate = "";
		}
		else {
			String[] splitResult = date.split("\\.");
			formattedDate = add(splitResult[0], 2) + ".";
			if (splitResult.length == 1) {
				formattedDate = formattedDate + add(String.valueOf(LocalDate.now().getMonthValue()), 2) + "." + add(String.valueOf(LocalDate.now().getYear()), 4);
			}
			else if (splitResult.length == 2) {
				formattedDate = formattedDate + add(splitResult[1], 2) + "." + add(String.valueOf(LocalDate.now().getYear()), 4);
			}
			else if (splitResult.length == 3) {
				formattedDate = formattedDate + add(splitResult[1], 2) + "." + add(splitResult[2], 4);
			}
		}
		return formattedDate;
	}

	private static String parseTimeString(String time) {
		String formattedTime = null;
		String[] splitResult = time.split(":");
		if (time.isEmpty()) {
			formattedTime = "";
		}
		else if (splitResult.length == 1) {
			formattedTime = add(splitResult[0], 2) + ":00";
		}
		else if (splitResult.length == 2) {
			formattedTime = add(splitResult[0], 2) + ":" + add(splitResult[1], 2);
		}
		return formattedTime;
	}

	private static boolean testDateString(String date) {
		try {
			LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}

	private static boolean testTimeString(String time) {
		try {
			LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}
}
