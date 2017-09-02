package database.plugin.expense;

import java.time.LocalDate;

import database.plugin.Instance;

public class Expense extends Instance {
	private String category;
	private LocalDate date;
	private String name;
	private Double value;
	private Currency currency = Currency.EUR;
	private double exchangeRate = 1;

	public Expense(String name, String category, Double value, LocalDate date) {
		this.name = name;
		this.category = category;
		this.value = value;
		setDate(date);
	}

	@Override
	public boolean equals(Object object) {
		if (object != null && object instanceof Expense) {
			Expense expense = (Expense) object;
			if (name.equals(expense.name) && category.equals(expense.category) && getDate().isEqual(expense.getDate())
					&& value == expense.value && currency.equals(expense.currency)
					&& exchangeRate == expense.exchangeRate) {
				return true;
			}
		}
		return false;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public double getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getCategory() {
		return category;
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

	public void setDate(LocalDate date) {
		this.date = date;
	}
}