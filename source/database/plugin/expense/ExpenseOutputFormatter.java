package database.plugin.expense;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;
import database.plugin.Command;
import database.plugin.OutputFormatter;

public class ExpenseOutputFormatter extends OutputFormatter<Expense> {
	private Map<String, Integer>	categoryAmount	= new HashMap<String, Integer>();
	private Map<String, Integer>	nameAmount		= new HashMap<String, Integer>();

	@Command(tag = "average") public String outputAverage(Iterable<Expense> iterable) {
		Expense first = iterable.iterator().next();
		DecimalFormat format = new DecimalFormat("#0.00");
		double totalValue = 0.0;
		String yearAverage;
		String monthAverage;
		String dayAverage;
		for (Expense expense : iterable) {
			if (!(expense.date.getMonthValue() == LocalDate.now().getMonthValue() && expense.date.getYear() == LocalDate.now().getYear())) {
				totalValue += expense.value;
			}
		}
		monthAverage = " - month : " + format.format(totalValue / ChronoUnit.MONTHS.between(first.date, LocalDate.now())) + "€";
		dayAverage = " - day   : " + format.format(totalValue / ChronoUnit.DAYS.between(first.date, LocalDate.now())) + "€";
		yearAverage = " - year  : " + format.format((totalValue / ChronoUnit.MONTHS.between(first.date, LocalDate.now()) * 12
														+ totalValue / ChronoUnit.DAYS.between(first.date, LocalDate.now()) * 365.25)
													/ 2)
						+ "€";
		while (yearAverage.length() > monthAverage.length()) {
			monthAverage = monthAverage.replaceFirst(": ", ":  ");
		}
		while (monthAverage.length() > dayAverage.length()) {
			dayAverage = dayAverage.replaceFirst(": ", ":  ");
		}
		return " average value per"+ System.getProperty("line.separator") + dayAverage + System.getProperty("line.separator") + monthAverage + System.getProperty("line.separator")
				+ yearAverage;
	}

	@Command(tag = "category") public String outputCategory(Iterable<Expense> iterable) {
		StringBuilder builder = new StringBuilder();
		DecimalFormat formatValue = new DecimalFormat("#0.00");
		DecimalFormat formatPercent = new DecimalFormat("00.00");
		double totalSum = 0;
		int longestName = 0;
		int longestValue = 0;
		int longestLine = 0;
		String name = null;
		Map<String, Double> categories = new TreeMap<String, Double>();
		for (Expense expense : iterable) {
			if (!categories.containsKey(expense.category)) {
				categories.put(expense.category, 0.0);
			}
			totalSum += expense.value;
		}
		for (String current : categories.keySet()) {
			if (current.length() > longestName) {
				longestName = current.length();
			}
			for (Expense expense : iterable) {
				if (expense.category.equals(current)) {
					categories.put(current, categories.get(current) + expense.value);
				}
			}
		}
		Map<String, Double> sortedMap = new LinkedHashMap<>();
		Stream<Entry<String, Double>> st = categories.entrySet().stream();
		st.sorted(Comparator.comparing(e -> e.getValue())).forEachOrdered(e -> sortedMap.put(e.getKey(), e.getValue()));
		categories = sortedMap;
		for (Double current : categories.values()) {
			if (formatValue.format(current).length() > longestValue) {
				longestValue = formatValue.format(current).length();
			}
		}
		for (Entry<String, Double> current : categories.entrySet()) {
			String value = formatPercent.format(current.getValue() / totalSum * 100);
			String line;
			name = " - " + current.getKey();
			while (name.length() < longestName + 7 + longestValue - formatValue.format(current.getValue()).length()) {
				name += " ";
			}
			if (value.startsWith("0")) {
				value = value.replaceFirst("0", " (");
			}
			else {
				value = "(" + value;
			}
			line = name + formatValue.format(current.getValue()) + "€  " + value + "%)";
			if (line.length() > longestLine) {
				longestLine = line.length();
			}
			builder.append(line + System.getProperty("line.separator"));
		}
		for (int i = 0; i < longestLine; i++) {
			builder.append("-");
		}
		builder.append(System.getProperty("line.separator"));
		for (int i = 0; i < name.length(); i++) {
			builder.append(" ");
		}
		builder.append(formatValue.format(totalSum) + "€");
		return builder.toString();
	}

	@Command(tag = "month") public String outputMonth(Iterable<Expense> iterable) {
		Map<String, Double> map = new LinkedHashMap<String, Double>();
		int valueLength = 0;
		String output = "";
		for (Expense expense : iterable) {
			String key = new DecimalFormat("00").format(expense.date.getMonthValue()) + "/" + String.valueOf(expense.date.getYear()).replaceFirst("20", "");
			map.put(key, map.getOrDefault(key, 0.0) + expense.value);
			int length = new DecimalFormat("#0.00").format(map.get(key)).length();
			if (length > valueLength) {
				valueLength = length;
			}
		}
		for (Entry<String, Double> entry : map.entrySet()) {
			String value = new DecimalFormat("#0.00").format(entry.getValue());
			while (value.length() < valueLength) {
				value = " " + value;
			}
			output += " " + entry.getKey() + " : " + value + "€" + System.getProperty("line.separator");
		}
		return output;
	}

	@Command(tag = "all") public String printAll(Iterable<Expense> iterable) {
		String print = outputAll(iterable);
		if (print.length() == 0) {
			print = "no entries";
		}
		return print;
	}

	@Command(tag = "current") public String printCurrent(Iterable<Expense> iterable) {
		StringBuilder builder = new StringBuilder();
		int longestValue = 0;
		for (Expense expense : iterable) {
			if (expense.date.getMonthValue() == LocalDate.now().getMonthValue() && expense.date.getYear() == LocalDate.now().getYear()) {
				if (new DecimalFormat("#0.00").format(expense.value).length() > longestValue) {
					longestValue = new DecimalFormat("#0.00").format(expense.value).length();
				}
			}
		}
		for (Expense expense : iterable) {
			if (expense.date.getMonthValue() == LocalDate.now().getMonthValue() && expense.date.getYear() == LocalDate.now().getYear()) {
				String name = expense.name;
				String value = new DecimalFormat("#0.00").format(expense.value) + "€";
				while (name.length() < 66) {
					name += " ";
				}
				while (value.length() <= longestValue) {
					value = " " + value;
				}
				builder.append(" \u2022 " + expense.date.format(DateTimeFormatter.ofPattern("dd/MM")) + " - " + name + " " + value + System.getProperty("line.separator"));
			}
		}
		return builder.toString();
	}

	protected void addExpense(Expense expense, Iterable<Expense> iterable) {
		if (!categoryAmount.containsKey(expense.category)) {
			categoryAmount.put(expense.category, 1);
		}
		else {
			categoryAmount.replace(expense.category, categoryAmount.get(expense.category) + 1);
		}
		if (!nameAmount.containsKey(expense.name)) {
			nameAmount.put(expense.name, 1);
		}
		else {
			nameAmount.replace(expense.name, nameAmount.get(expense.name) + 1);
		}
	}

	protected String getCategoryByString(String name, Iterable<Expense> iterable) {
		for (Expense expense : iterable) {
			if (expense.category.equalsIgnoreCase(name)) {
				return expense.category;
			}
		}
		return name;
	}

	@Override protected String getInitialOutput(Iterable<Expense> iterable) {
		return "todo";
	}

	protected String getMostUsedCategoryByPrefixAndName(String prefix, String name, Iterable<Expense> iterable) {
		String mostUsedCategory = "";
		for (Entry<String, Integer> entry : categoryAmount.entrySet()) {
			if (categoryContainsName(entry.getKey(), name, iterable)) {
				if (entry.getKey().toLowerCase().startsWith(prefix.toLowerCase())
					&& (categoryAmount.get(mostUsedCategory) == null || entry.getValue() > categoryAmount.get(mostUsedCategory))) {
					mostUsedCategory = entry.getKey();
				}
			}
		}
		return !nameExists(name, iterable) && prefix.isEmpty() ? "" : mostUsedCategory.isEmpty() ? "" : mostUsedCategory.substring(prefix.length());
	}

	protected String getMostUsedNameByPrefix(String prefix, Iterable<Expense> iterable) {
		String mostUsedName = "";
		for (Entry<String, Integer> entry : nameAmount.entrySet()) {
			if (entry.getKey().toLowerCase().startsWith(prefix.toLowerCase()) && (nameAmount.get(mostUsedName) == null || entry.getValue() > nameAmount.get(mostUsedName))) {
				mostUsedName = entry.getKey();
			}
		}
		return prefix.isEmpty() ? "" : mostUsedName.isEmpty() ? "" : mostUsedName.substring(prefix.length());
	}

	protected String getNameByString(String name, Iterable<Expense> iterable) {
		for (Expense expense : iterable) {
			if (expense.name.equalsIgnoreCase(name)) {
				return expense.name;
			}
		}
		return name;
	}

	private boolean categoryContainsName(String category, String name, Iterable<Expense> iterable) {
		for (Expense expense : iterable) {
			if (expense.category.equals(category) && expense.name.equals(name)) {
				return true;
			}
		}
		return !nameExists(name, iterable);
	}

	private boolean nameExists(String name, Iterable<Expense> iterable) {
		for (Expense expense : iterable) {
			if (expense.name.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private String outputAll(Iterable<Expense> iterable) {
		DecimalFormat format = new DecimalFormat("#0.00");
		int nameLength = 0;
		int valueLength = 0;
		int blankCounter;
		String blanks;
		String toReturn = "";
		ArrayList<String> categories = new ArrayList<String>();
		Map<String, Double> names;
		for (Expense expense : iterable) {
			if (!categories.contains(expense.category)) {
				categories.add(expense.category);
			}
			if (expense.name.length() > nameLength) {
				nameLength = expense.name.length();
			}
			if (format.format(expense.value).length() > valueLength) {
				valueLength = format.format(expense.value).length();
			}
		}
		Collections.sort(categories);
		for (String current : categories) {
			toReturn = toReturn + current + ":" + System.getProperty("line.separator");
			names = new LinkedHashMap<String, Double>();
			for (Expense expense : iterable) {
				if (expense.category.equals(current)) {
					names.put(expense.name, 0.0);
				}
			}
			for (String name : names.keySet()) {
				for (Expense expense : iterable) {
					if (expense.category.equals(current) && expense.name.equals(name)) {
						names.replace(name, names.get(name) + expense.value);
					}
				}
			}
			Map<String, Double> map = new LinkedHashMap<String, Double>();
			Stream<Map.Entry<String, Double>> st = names.entrySet().stream();
			st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> map.put(e.getKey(), e.getValue()));
			for (Entry<String, Double> entry : map.entrySet()) {
				blanks = "       ";
				toReturn = toReturn + "  - " + entry.getKey();
				blankCounter = nameLength - entry.getKey().length() + 1 + valueLength - format.format(entry.getValue()).length();
				for (int i = 0; i < blankCounter; i++) {
					blanks = blanks + " ";
				}
				toReturn = toReturn + blanks + format.format(entry.getValue()) + "€" + System.getProperty("line.separator");
			}
		}
		return toReturn;
	}
}