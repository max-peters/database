package database.plugin.expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.w3c.dom.Element;
import database.plugin.Instance;

public class Expense extends Instance {
	public String		category;
	public LocalDate	date;
	public String		name;
	public Double		value;

	public Expense(String name, String category, Double value, LocalDate date) {
		this.name = name;
		this.category = category;
		this.value = value;
		this.date = date;
	}

	@Override public boolean equals(Object object) {
		Expense expense;
		if (object != null && object.getClass().equals(this.getClass())) {
			expense = (Expense) object;
			if (name.equals(expense.name) && category.equals(expense.category) && date.isEqual(expense.date) && value.equals(expense.value)) {
				return true;
			}
		}
		return false;
	}

	@Override public void insertParameter(Element element) {
		element.setAttribute("name", name);
		element.setAttribute("category", category);
		element.setAttribute("value", value.toString());
		element.setAttribute("date", date.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
	}
}