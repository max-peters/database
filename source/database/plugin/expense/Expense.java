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

	protected String getName() {
		return getParameter("name");
	}

	protected String getCategory() {
		return getParameter("category");
	}

	protected Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	@Override public int compareTo(Instance instance) {
		if (instance instanceof Expense) {
			return getDate().compareTo(((Expense) instance).getDate());
		}
		else {
			return 0;
		}
	}

	protected boolean checkValidity(int month, int year) {
		if ((getDate().month.counter == month && getDate().year.counter == year) || (month < 0 && year < 0)) {
			return true;
		}
		return false;
	}
}