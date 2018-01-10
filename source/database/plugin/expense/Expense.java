package database.plugin.expense;

import java.time.LocalDate;

import database.plugin.Instance;

public class Expense extends Instance {
	private String category;
	private String currency = Currency.EUR;
	private LocalDate date;
	private String name;
	private Double value;

	public Expense(String name, String category, Double value, LocalDate date, String currency) {
		this.name = name;
		this.category = category;
		this.value = value;
		this.currency = currency;
		this.date = date;
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Expense) {
			Expense expense = (Expense) object;
			if (name.equals(expense.name) && category.equals(expense.category) && getDate().isEqual(expense.getDate())
					&& value == expense.value && currency.equals(expense.currency)) {
				return true;
			}
		}
		return false;
	}

	public String getCategory() {
		return category;
	}

	public String getCurrency() {
		return currency;
	}

	public LocalDate getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public Double getValue() {
		return value;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}