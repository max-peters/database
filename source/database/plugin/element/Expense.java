package database.plugin.element;

import java.time.LocalDate;
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
		if (object != null && object instanceof Expense) {
			Expense expense = (Expense) object;
			if (name.equals(expense.name) && category.equals(expense.category) && date.isEqual(expense.date) && value == expense.value) {
				return true;
			}
		}
		return false;
	}
}