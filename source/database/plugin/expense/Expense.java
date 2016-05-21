package database.plugin.expense;

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
}