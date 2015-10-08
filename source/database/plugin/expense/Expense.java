package database.plugin.expense;

import java.util.Map;

import database.main.date.Date;
import database.plugin.Instance;

public class Expense extends Instance {
	public Expense(Map<String, String> parameter, ExpenseList list) {
		super(parameter, list);
	}

	protected Date getDate() {
		return new Date(getParameter("date"));
	}

	public String getName() {
		return getParameter("name");
	}

	public String getCategory() {
		return getParameter("category");
	}

	public Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	protected boolean checkValidity(int month, int year) {
		if ((getDate().month.counter == month && getDate().year.counter == year) || (month < 0 && year < 0)) {
			return true;
		}
		return false;
	}
}