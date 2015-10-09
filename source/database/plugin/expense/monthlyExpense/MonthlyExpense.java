package database.plugin.expense.monthlyExpense;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import database.main.date.Date;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpenseList;

public class MonthlyExpense extends Expense {
	public MonthlyExpense(Map<String, String> parameter, ExpenseList expenseList) {
		super(parameter);
		createExpense(parameter, expenseList);
	}

	public void createExpense(Map<String, String> parameter, ExpenseList expenseList) {
		Date date = new Date(parameter.get("date"));
		while (date.isPast() || date.isToday()) {
			if (!expenseList.contains(parameter)) {
				expenseList.add(parameter);
			}
			if (date.month.counter + 1 == 13) {
				date = new Date(date.day.counter + ".01." + (date.year.counter + 1));
			}
			else {
				date = new Date(date.day.counter + "." + (date.month.counter + 1) + "." + date.year.counter);
			}
			Map<String, String> map = new HashMap<String, String>();
			for (Entry<String, String> entry : parameter.entrySet()) {
				map.put(entry.getKey(), entry.getValue());
			}
			map.replace("date", date.toString());
			parameter = map;
		}
	}
}
