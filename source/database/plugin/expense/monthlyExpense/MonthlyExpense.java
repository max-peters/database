package database.plugin.expense.monthlyExpense;

import java.util.Map;

import database.main.date.Date;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpenseList;

public class MonthlyExpense extends Expense {
	public MonthlyExpense(Map<String, String> parameter, MonthlyExpenseList list, ExpenseList expenseList) {
		super(parameter, list);
		createExpense(parameter, expenseList);
	}

	public void createExpense(Map<String, String> parameter, ExpenseList list) {
		Date date = new Date(parameter.get("date"));
		while (date.isPast() || date.isToday()) {
			if (!list.contains(parameter)) {
				list.add(parameter);
			}
			if (date.month.counter + 1 == 13) {
				date = new Date(date.day.counter + ".01." + date.year.counter + 1);
			}
			else {
				date = new Date(date.day.counter + "." + date.month.counter + 1 + "." + date.year.counter);
			}
			parameter.replace("date", date.toString());
		}
	}
}
