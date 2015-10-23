package database.plugin.expense.monthlyExpense;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import database.main.Terminal;
import database.main.date.Date;
import database.plugin.InstanceList;
import database.plugin.expense.ExpenseList;

public class MonthlyExpenseList extends InstanceList {
	private ExpenseList expenseList;

	public MonthlyExpenseList(ExpenseList expenseList) {
		super();
		this.expenseList = expenseList;
	}

	@Override public void add(Map<String, String> parameter) {
		getList().add(new MonthlyExpense(parameter));
		createExpense(parameter, expenseList);
	}

	private void createExpense(Map<String, String> parameter, ExpenseList expenseList) {
		Date date = new Date(parameter.get("date"));
		while (date.isPast() || date.isToday()) {
			if (!expenseList.contains(parameter)) {
				expenseList.add(parameter);
				Terminal.collectStartInformation("expense created");
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
