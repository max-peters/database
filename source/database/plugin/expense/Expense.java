package database.plugin.expense;

import java.util.Map;

import database.main.date.Date;
import database.plugin.Instance;

public class Expense extends Instance {
	public Expense(Map<String, String> parameter, ExpenseList list) {
		super(parameter, list);
		assert parameter.size() == 4;
		assert parameter.containsKey("date");
		assert parameter.containsKey("name");
		assert parameter.containsKey("category");
		assert parameter.containsKey("value");
	}

	protected Date getDate() {
		return new Date(getParameter("date"));
	}

	protected String getName() {
		return getParameter("name");
	}

	protected String getCategory() {
		return getParameter("category");
	}

	protected Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	protected boolean checkValidity(int month, int year) {
		if ((getDate().month.counter == month && getDate().year.counter == year) || (month < 0 && year < 0)) {
			return true;
		}
		return false;
	}
}