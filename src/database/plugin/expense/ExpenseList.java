package database.plugin.expense;

import java.text.DecimalFormat;
import java.util.ArrayList;

import database.date.Date;
import database.date.Month;
import database.main.Instance;
import database.main.InstanceList;

public class ExpenseList extends InstanceList {
	public ArrayList<Expense> getList() {
		ArrayList<Expense> expenses = new ArrayList<Expense>();
		for (Instance instance : list) {
			expenses.add((Expense) instance);
		}
		return expenses;
	}

	public void add(String[] parameter) {
		list.add(new Expense(parameter, this));
	}

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		String print = null;
		switch (tags[0]) {
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

	public String initialOutput() {
		return null;
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
		for (Expense current : getList()) {
			if (!(categorys.contains(current.category))) {
				categorys.add(current.category);
			}
			if (current.name.length() > nameLength) {
				nameLength = current.name.length();
			}
			if (format.format(current.value).length() > valueLength) {
				valueLength = format.format(current.value).length();
			}
		}
		for (String current : categorys) {
			toReturn = toReturn + current + ":" + "\r\n";
			names = new ArrayList<String>();
			for (Expense currentExpense : getList()) {
				if (currentExpense.checkValidity(month, year) && (currentExpense.category.equals(current)) && !(names.contains(currentExpense.name))) {
					names.add(currentExpense.name);
				}
			}
			for (String name : names) {
				value = 0;
				blanks = "";
				toReturn = toReturn + "-" + name;
				for (Expense currentExpense : getList()) {
					if (currentExpense.checkValidity(month, year) && (currentExpense.name.equals(name))) {
						value = value + currentExpense.value;
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
		for (Expense current : getList()) {
			if (current.checkValidity(month, year)) {
				value = value + current.value;
			}
		}
		return value;
	}

	private ArrayList<Month> getMonths() {
		ArrayList<Month> months = new ArrayList<Month>();
		for (Expense current : getList()) {
			if (!months.contains(current.date.month)) {
				months.add(current.date.month);
			}
		}
		return months;
	}
}