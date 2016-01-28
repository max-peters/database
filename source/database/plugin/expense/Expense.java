package database.plugin.expense;

import java.util.HashMap;
import java.util.Map;
import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Instance;

public class Expense extends Instance {
	public String	category;
	public Date		date;
	public String	name;
	public Double	value;

	public Expense(Map<String, String> parameter) {
		category = parameter.get("category");
		name = parameter.get("name");
		date = new Date(parameter.get("date"));
		value = Double.valueOf(parameter.get("value"));
	}

	@Override public boolean equals(Object object) {
		Expense expense;
		if (object != null && object.getClass().equals(this.getClass())) {
			expense = (Expense) object;
			if (name.equals(expense.name) && category.equals(expense.category) && date.equals(expense.date) && value.equals(expense.value)) {
				return true;
			}
		}
		return false;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("name", name);
		parameter.put("category", category);
		parameter.put("value", value.toString());
		parameter.put("date", date.toString());
		return parameter;
	}

	protected boolean checkValidity(Month month) {
		if (date.month.equals(month) || month == null) {
			return true;
		}
		return false;
	}
}