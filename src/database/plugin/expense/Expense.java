package database.plugin.expense;

import database.main.date.Date;
import database.plugin.Instance;

public class Expense extends Instance {
	public Expense(String[][] parameter, ExpenseList list) {
		super(parameter, parameter[2][1], list);
	}

	@Override public String[][] getParameter() {
		return new String[][] { { "name", getParameter("name") }, { "category", getParameter("category") }, { "value", getParameter("value") }, { "date", getParameter("date") } };
	}

	protected boolean checkValidity(int month, int year) {
		boolean returnValue = false;
		if ((new Date(getParameter("date")).month.counter == month && new Date(getParameter("date")).year.counter == year) || (month < 0 && year < 0)) {
			returnValue = true;
		}
		return returnValue;
	}
}