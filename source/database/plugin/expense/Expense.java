package database.plugin.expense;

import org.w3c.dom.Element;
import database.main.date.Date;
import database.main.date.Month;
import database.plugin.Instance;

public class Expense extends Instance {
	public String	category;
	public Date		date;
	public String	name;
	public Double	value;

	public Expense(String name, String category, Double value, Date date) {
		this.name = name;
		this.category = category;
		this.value = value;
		this.date = date;
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

	@Override public void insertParameter(Element element) {
		element.setAttribute("name", name);
		element.setAttribute("category", category);
		element.setAttribute("value", value.toString());
		element.setAttribute("date", date.toString());
	}

	protected boolean checkValidity(Month month) {
		if (date.month.equals(month) || month == null) {
			return true;
		}
		return false;
	}
}