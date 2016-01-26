package database.plugin.expense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Stream;
import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class ExpenseList extends InstanceList {
	@Override public void add(Map<String, String> parameter) {
		Expense expense = new Expense(parameter);
		int i = list.size();
		while (i > 0 && ((Expense) list.get(i - 1)).date.compareTo(expense.date) > 0) {
			i--;
		}
		list.add(i, expense);
	}

	@Override public String initialOutput() {
		DecimalFormat format = new DecimalFormat("#0.00");
		return "total expenditure this mounth: " + format.format(value(Date.getCurrentDate().month)) + "€";
	}

	@Command(tag = "day") public String outputAveragePerDay() {
		DecimalFormat format = new DecimalFormat("#0.00");
		double value = 0;
		int dayCounter = 0;
		for (Month month : getMonths()) {
			value = value + value(month);
			dayCounter = dayCounter + month.getDayCount();
		}
		return "average value per day: " + format.format(value / dayCounter) + "€";
	}

	@Command(tag = "average") public String outputAveragePerMonth() {
		DecimalFormat format = new DecimalFormat("#0.00");
		double value = 0;
		for (Month month : getMonths()) {
			value = value + value(month);
		}
		return "average value per month: " + format.format(value / getMonths().size()) + "€";
	}

	@Command(tag = "category") public String outputCategory() {
		StringBuilder builder = new StringBuilder();
		DecimalFormat formatValue = new DecimalFormat("#0.00");
		DecimalFormat formatPercent = new DecimalFormat("00.00");
		double totalSum = 0;
		int longestName = 0;
		int longestValue = 0;
		int longestLine = 0;
		String name = null;
		Map<String, Double> categories = new TreeMap<String, Double>();
		for (Instance instance : getIterable()) {
			Expense expense = (Expense) instance;
			if (expense.checkValidity(null) && !categories.containsKey(expense.category)) {
				categories.put(expense.category, 0.0);
			}
			totalSum += expense.value;
		}
		for (String current : categories.keySet()) {
			if (current.length() > longestName) {
				longestName = current.length();
			}
			for (Instance instance : getIterable()) {
				Expense expense = (Expense) instance;
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

	@Command(tag = "month") public String outputMonth() {
		DecimalFormat format = new DecimalFormat("#0.00");
		String output = "";
		for (Month month : getMonths()) {
			output = output+ String.format("%2s", month.counter).replace(" ", "0") + "/" + month.year.counter + " : " + format.format(value(month)) + "€"
						+ System.getProperty("line.separator");
		}
		return output;
	}

	@Command(tag = "all") public String printAll() {
		String print = outputIntervall(null);
		if (print.length() == 0) {
			print = "no entries";
		}
		return print;
	}

	@Command(tag = "current") public String printCurrent() {
		String print = outputIntervall(Date.getCurrentDate().month);
		if (print.length() == 0) {
			print = "no entries";
		}
		return print;
	}

	private ArrayList<Month> getMonths() {
		ArrayList<Month> months = new ArrayList<Month>();
		for (Instance instance : getIterable()) {
			Expense expense = (Expense) instance;
			if (!months.contains(expense.date.month)) {
				months.add(expense.date.month);
			}
		}
		return months;
	}

	private String outputIntervall(Month month) {
		DecimalFormat format = new DecimalFormat("#0.00");
		int nameLength = 0;
		int valueLength = 0;
		int blankCounter;
		double value;
		String blanks;
		String toReturn = "";
		ArrayList<String> categories = new ArrayList<String>();
		ArrayList<String> names;
		for (Instance instance : getIterable()) {
			Expense expense = (Expense) instance;
			if (expense.checkValidity(month) && !categories.contains(expense.category)) {
				categories.add(expense.category);
			}
			if (expense.checkValidity(month) && expense.name.length() > nameLength) {
				nameLength = expense.name.length();
			}
			if (expense.checkValidity(month) && format.format(expense.value).length() > valueLength) {
				valueLength = format.format(expense.value).length();
			}
		}
		// Collections.sort(categories); nach wichtigkeit ordnen
		for (String current : categories) {
			toReturn = toReturn + current + ":" + System.getProperty("line.separator");
			names = new ArrayList<String>();
			for (Instance instance : getIterable()) {
				Expense expense = (Expense) instance;
				if (expense.checkValidity(month) && expense.category.equals(current) && !names.contains(expense.name)) {
					names.add(expense.name);
				}
			}
			Collections.sort(names);
			for (String name : names) {
				value = 0;
				blanks = "      ";
				toReturn = toReturn + "  - " + name;
				for (Instance instance : getIterable()) {
					Expense expense = (Expense) instance;
					if (expense.checkValidity(month) && expense.category.equals(current) && expense.name.equals(name)) {
						value = value + expense.value;
					}
				}
				blankCounter = nameLength - name.length() + 1 + valueLength - format.format(value).length();
				for (int i = 0; i < blankCounter; i++) {
					blanks = blanks + " ";
				}
				toReturn = toReturn + blanks + "  " + format.format(value) + "€" + System.getProperty("line.separator");
			}
		}
		return toReturn;
	}

	private double value(Month month) {
		double value = 0;
		for (Instance instance : getIterable()) {
			Expense expense = (Expense) instance;
			if (expense.checkValidity(month)) {
				value = value + expense.value;
			}
		}
		return value;
	}
}