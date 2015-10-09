package database.plugin.expense;

import java.util.Map;

import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Instance;

public class Expense extends Instance {
	public Expense(Map<String, String> parameter) {
		super(parameter);
		parameter.replace("value", String.valueOf(Double.valueOf(getParameter("value"))));
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

	protected boolean checkValidity(Month month) {
		if (getDate().month.equals(month) || month == null) {
			return true;
		}
		return false;
	}
}