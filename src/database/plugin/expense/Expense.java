package database.plugin.expense;

import database.main.date.Date;
import database.plugin.Instance;

public class Expense extends Instance {
	public Expense(String[][] parameter, ExpenseList list) {
		super(parameter, parameter[2][1], list);
	}

	Date getDate() {
		return new Date(getParameter("date"));
	}

	String getName() {
		return getParameter("name");
	}

	String getCategory() {
		return getParameter("category");
	}

	Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	protected boolean checkValidity(int month, int year) {
		boolean returnValue = false;
		if ((getDate().month.counter == month && getDate().year.counter == year) || (month < 0 && year < 0)) {
			returnValue = true;
		}
		return returnValue;
	}
}