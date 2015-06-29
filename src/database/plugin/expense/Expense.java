package database.plugin.expense;

import database.date.Date;
import database.main.Instance;

public class Expense extends Instance {
	public double	value;
	public Date		date;
	public String	name;
	public String	category;

	public Expense(String[] parameter, ExpenseList list) {
		super(parameter[0], list);
		this.name = parameter[0];
		this.category = parameter[1];
		this.value = Double.valueOf(parameter[2]);
		this.date = new Date(parameter[3]);
	}

	public String toString() {
		return "expense" + " : " + name + " / " + category + " / " + value + " / " + date.toString();
	}

	protected boolean checkValidity(int month, int year) {
		boolean returnValue = false;
		if ((date.month.counter == month && date.year.counter == year) || (month < 0 && year < 0)) {
			returnValue = true;
		}
		return returnValue;
	}
}