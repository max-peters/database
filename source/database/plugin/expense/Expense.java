package database.plugin.expense;

import java.util.HashMap;
import java.util.Map;
import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Instance;

public class Expense extends Instance {
	public String	category;
	public String	name;
	public Date		date;
	public Double	value;

	public Expense(Map<String, String> parameter) {
		this.category = parameter.get("category");
		this.name = parameter.get("name");
		this.date = new Date(parameter.get("date"));
		this.value = Double.valueOf(parameter.get("value"));
	}

	protected boolean checkValidity(Month month) {
		if (date.month.equals(month) || month == null) {
			return true;
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
}