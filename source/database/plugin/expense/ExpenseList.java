package database.plugin.expense;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class ExpenseList extends InstanceList {
	@Override public void add(Map<String, String> parameter) {
		Expense expense = new Expense(parameter, this);
		int i = getList().size();
		while (i > 0 && ((Expense) getList().get(i - 1)).getDate().compareTo(expense.getDate()) > 0) {
			i--;
		}
		getList().add(i, expense);
	}

	@Override public String output(Map<String, String> map) {
		String print = null;
		switch (map.get("show")) {
			case "all":
				print = outputIntervall(-1, -1);
				if (print.length() == 0) {
					print = "no entries";
				}
				break;
			case "current":
				print = outputIntervall(Date.getDate().month.counter, Date.getDate().year.counter);
				if (print.length() == 0) {
					print = "no entries";
				}
				break;
			case "average":
				print = outputAverageMonth();
				break;
			case "month":
				print = outputMonth();
				break;
			case "day":
				print = outputAverageDay();
				break;
		}
		return print;
	}

	@Override public String initialOutput() {
		DecimalFormat format = new DecimalFormat("#0.00");
		return "total expenditure this mounth: " + format.format(value(Date.getDate().month.counter, Date.getDate().year.counter)) + "€";
	}

	private String outputIntervall(int month, int year) {
		DecimalFormat format = new DecimalFormat("#0.00");
		int nameLength = 0;
		int valueLength = 0;
		int blankCounter;
		double value;
		String blanks;
		String toReturn = "";
		ArrayList<String> categories = new ArrayList<String>();
		ArrayList<String> names;
		for (Instance instance : getList()) {
			Expense expense = (Expense) instance;
			if (expense.checkValidity(month, year) && !(categories.contains(expense.getCategory()))) {
				categories.add(expense.getCategory());
			}
			if (expense.checkValidity(month, year) && expense.getName().length() > nameLength) {
				nameLength = expense.getName().length();
			}
			if (expense.checkValidity(month, year) && format.format(expense.getValue()).length() > valueLength) {
				valueLength = format.format(expense.getValue()).length();
			}
		}
		// Collections.sort(categories); nach wichtigkeit ordnen
		for (String current : categories) {
			toReturn = toReturn + current + ":\r\n";
			names = new ArrayList<String>();
			for (Instance instance : getList()) {
				Expense expense = (Expense) instance;
				if (expense.checkValidity(month, year) && expense.getCategory().equals(current) && !(names.contains(expense.getName()))) {
					names.add(expense.getName());
				}
			}
			Collections.sort(names);
			for (String name : names) {
				value = 0;
				blanks = "";
				toReturn = toReturn + "-" + name;
				for (Instance instance : getList()) {
					Expense expense = (Expense) instance;
					if (expense.checkValidity(month, year) && expense.getCategory().equals(current) && expense.getName().equals(name)) {
						value = value + expense.getValue();
					}
				}
				blankCounter = (nameLength - name.length() + 1) + valueLength - format.format(value).length();
				for (int i = 0; i < blankCounter; i++) {
					blanks = blanks + " ";
				}
				toReturn = toReturn + blanks + "  " + format.format(value) + "€" + "\r\n";
			}
		}
		return toReturn;
	}

	private String outputAverageMonth() {
		DecimalFormat format = new DecimalFormat("#0.00");
		double value = 0;
		for (Month month : getMonths()) {
			value = value + value(month.counter, month.year.counter);
		}
		return "average value per month: " + format.format(value / getMonths().size()) + "€";
	}

	private String outputAverageDay() {
		DecimalFormat format = new DecimalFormat("#0.00");
		double value = 0;
		int dayCounter = 0;
		for (Month month : getMonths()) {
			value = value + value(month.counter, month.year.counter);
			dayCounter = dayCounter + month.dayCount;
		}
		return "average value per day: " + format.format(value / dayCounter) + "€";
	}

	private String outputMonth() {
		DecimalFormat format = new DecimalFormat("#0.00");
		String output = "";
		for (Month month : getMonths()) {
			output = output + String.format("%2s", month.counter).replace(" ", "0") + "/" + month.year.counter + " : " + format.format(value(month.counter, month.year.counter)) + "€" + "\r\n";
		}
		return output;
	}

	private double value(int month, int year) {
		double value = 0;
		for (Instance instance : getList()) {
			Expense expense = (Expense) instance;
			if (expense.checkValidity(month, year)) {
				value = value + expense.getValue();
			}
		}
		return value;
	}

	private ArrayList<Month> getMonths() {
		ArrayList<Month> months = new ArrayList<Month>();
		for (Instance instance : getList()) {
			Expense expense = (Expense) instance;
			if (!months.contains(expense.getDate().month)) {
				months.add(expense.getDate().month);
			}
		}
		return months;
	}
}