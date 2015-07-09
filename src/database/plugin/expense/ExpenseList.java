package database.plugin.expense;

import java.text.DecimalFormat;
import java.util.ArrayList;

import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class ExpenseList extends InstanceList {
	@Override public void add(String[][] parameter) {
		getList().add(new Expense(parameter, this));
	}

	@Override public String output(String[][] tags) {
		String print = null;
		switch (tags[0][1]) {
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

	private String outputIntervall(int month, int year) {
		DecimalFormat format = new DecimalFormat("#0.00");
		int nameLength = 0;
		int valueLength = 0;
		int blankCounter;
		double value;
		String blanks;
		String toReturn = "";
		ArrayList<String> categorys = new ArrayList<String>();
		ArrayList<String> names;
		for (Instance instance : getList()) {
			Expense expense = (Expense) instance;
			if (!(categorys.contains(expense.getParameter("category")))) {
				categorys.add(expense.getParameter("category"));
			}
			if (expense.getParameter("name").length() > nameLength) {
				nameLength = expense.getParameter("name").length();
			}
			if (format.format(Double.valueOf(expense.getParameter("value"))).length() > valueLength) {
				valueLength = format.format(Double.valueOf(expense.getParameter("value"))).length();
			}
		}
		for (String current : categorys) {
			toReturn = toReturn + current + ":\r\n";
			names = new ArrayList<String>();
			for (Instance instance : getList()) {
				Expense expense = (Expense) instance;
				if (expense.checkValidity(month, year) && (expense.getParameter("category").equals(current)) && !(names.contains(expense.getParameter("name")))) {
					names.add(expense.getParameter("name"));
				}
			}
			for (String name : names) {
				value = 0;
				blanks = "";
				toReturn = toReturn + "-" + name;
				for (Instance instance : getList()) {
					Expense expense = (Expense) instance;
					if (expense.checkValidity(month, year) && (expense.getParameter("name").equals(name))) {
						value = value + Double.valueOf(expense.getParameter("value"));
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
				value = value + Double.valueOf(expense.getParameter("value"));
			}
		}
		return value;
	}

	private ArrayList<Month> getMonths() {
		ArrayList<Month> months = new ArrayList<Month>();
		for (Instance instance : getList()) {
			Expense expense = (Expense) instance;
			if (!months.contains(new Date(expense.getParameter("date")).month)) {
				months.add(new Date(expense.getParameter("date")).month);
			}
		}
		return months;
	}
}