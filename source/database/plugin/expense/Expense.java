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

	public String getCategory() {
		return getParameter("category");
	}

	public String getName() {
		return getParameter("name");
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

	protected Date getDate() {
		return new Date(getParameter("date"));
	}
}