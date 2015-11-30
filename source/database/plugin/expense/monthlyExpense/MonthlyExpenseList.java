package database.plugin.expense.monthlyExpense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import database.main.Terminal;
import database.main.GraphicalUserInterface.StringFormat;
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
		List<Map<String, String>> list = createExpense(parameter, expenseList);
		if (!list.isEmpty()) {
			Terminal.collectLine("expense created:", StringFormat.ITALIC);
		}
		for (Map<String, String> map : list) {
			for (Entry<String, String> entry : map.entrySet()) {
				Terminal.collectLine(entry.getValue(), StringFormat.STANDARD);
			}
		}
	}

	private List<Map<String, String>> createExpense(Map<String, String> parameter, ExpenseList expenseList) {
		Date date = new Date(parameter.get("date"));
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		while (date.isPast() || date.isToday()) {
			if (!expenseList.contains(parameter)) {
				expenseList.add(parameter);
				list.add(parameter);
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
		return list;
	}
}
