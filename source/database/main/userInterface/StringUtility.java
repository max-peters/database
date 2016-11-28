package database.main.userInterface;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

public class StringUtility {
	public String arrangeInCollums(List<List<String>> list, int gapSize) {
		int lenght = 0;
		int size = 0;
		StringBuilder sb = new StringBuilder();
		for (List<String> innerList : list) {
			if (innerList.size() > size) {
				size = innerList.size();
			}
			for (String string : innerList) {
				if (string.length() > lenght) {
					lenght = string.length();
				}
			}
		}
		for (List<String> innerList : list) {
			while (innerList.size() < size) {
				innerList.add("");
			}
		}
		for (int j = 0; j < size; j++) {
			for (List<String> innerList : list) {
				String temp = innerList.get(j);
				while (temp.length() < lenght + gapSize) {
					temp += " ";
				}
				sb.append(temp);
			}
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

	public int findString(int index, char firstLetter, Collection<String> collection) {
		int i = 1;
		for (String string : collection) {
			if (i > index && Character.toUpperCase(string.charAt(0)) == Character.toUpperCase(firstLetter)) {
				return i;
			}
			i++;
		}
		return index;
	}

	public String formatCheckLine(Collection<String> collection, int currentLine) {
		String output = "";
		int counter = 1;
		for (String string : collection) {
			if (counter == currentLine) {
				output += " \u2611 ";
			}
			else {
				output += " \u2610 ";
			}
			output += string + System.getProperty("line.separator");
			counter++;
		}
		return output;
	}

	public String getElementWithDistance(String input, String[] splitResult, int levenshteinDistance) {
		for (String string : splitResult) {
			if (levenshteinDistance(input, string) < levenshteinDistance) {
				return string;
			}
		}
		return null;
	}

	public String parseDateString(String date) {
		String formattedDate = null;
		if (date.isEmpty()) {
			formattedDate = "";
		}
		else {
			String[] splitResult = date.split("\\.");
			formattedDate = fill(splitResult[0], 2) + ".";
			if (splitResult.length == 1) {
				formattedDate = formattedDate + fill(String.valueOf(LocalDate.now().getMonthValue()), 2) + "." + fill(String.valueOf(LocalDate.now().getYear()), 4);
			}
			else if (splitResult.length == 2) {
				formattedDate = formattedDate + fill(splitResult[1], 2) + "." + fill(String.valueOf(LocalDate.now().getYear()), 4);
			}
			else if (splitResult.length == 3) {
				formattedDate = formattedDate + fill(splitResult[1], 2) + "." + fill(splitResult[2], 4);
			}
		}
		return formattedDate;
	}

	public String parseTimeString(String time) {
		String formattedTime = null;
		String[] splitResult = time.split(":");
		if (time.isEmpty()) {
			formattedTime = "";
		}
		else if (splitResult.length == 1) {
			formattedTime = fill(splitResult[0], 2) + ":00";
		}
		else if (splitResult.length == 2) {
			formattedTime = fill(splitResult[0], 2) + ":" + fill(splitResult[1], 2);
		}
		return formattedTime;
	}

	public boolean testDateString(String date) {
		try {
			LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}

	public boolean testTimeString(String time) {
		try {
			LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
			return true;
		}
		catch (DateTimeParseException e) {
			return false;
		}
	}

	private String fill(String string, int k) {
		for (string.length(); string.length() < k;) {
			string = "0" + string;
		}
		return string;
	}

	private int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
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
}
