package database.plugin.expense.monthlyExpense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import database.main.date.Date;
import database.main.date.Month;
import database.main.date.Year;
import database.main.userInterface.StringFormat;
import database.main.userInterface.Terminal;
import database.plugin.InstanceList;
import database.plugin.expense.ExpenseList;

public class MonthlyExpenseList extends InstanceList {
	private ExpenseList expenseList;

	public MonthlyExpenseList(ExpenseList expenseList) {
		super();
		this.expenseList = expenseList;
	}

	@Override public void add(Map<String, String> parameter) {
		StringBuilder builder = new StringBuilder();
		getList().add(new MonthlyExpense(parameter));
		Map<String, String> parameterCopy = new HashMap<String, String>();
		parameterCopy.putAll(parameter);
		List<Map<String, String>> list = createExpense(parameterCopy, expenseList, ExecutionDay.getExecutionDay(parameterCopy.remove("executionday")));
		for (Map<String, String> map : list) {
			builder.append(" - " + map.get("date") + "  " + map.get("name") + " (" + map.get("category") + ")  " + map.get("value") + "€");
			builder.append(System.getProperty("line.separator"));
		}
		if (!list.isEmpty()) {
			Terminal.collectLine(builder.toString(), StringFormat.STANDARD);
		}
	}

	private Date adjustDate(int month, int year, ExecutionDay executionDay) {
		Date date = null;
		switch (executionDay) {
			case FIRST:
				date = new Date("01." + month + "." + year);
				break;
			case LAST:
				date = new Date(new Month(month, new Year(year)).getDayCount() + "." + month + "." + year);
				break;
			case MID:
				date = new Date(new Month(month, new Year(year)).getDayCount() / 2 + "." + month + "." + year);
				break;
		}
		return date;
	}

	private List<Map<String, String>> createExpense(Map<String, String> parameter, ExpenseList expenseList, ExecutionDay executionDay) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Date date = new Date(parameter.get("date"));
		date = adjustDate(date.month.counter, date.year.counter, executionDay);
		parameter.put("date", date.toString());
		while (date.isPast() || date.isToday()) {
			Map<String, String> parameterCopy = new HashMap<String, String>();
			parameterCopy.putAll(parameter);
			parameterCopy.remove("value");
			if (!expenseList.containsParts(parameterCopy)) {
				expenseList.add(parameter);
				list.add(parameter);
			}
			if (date.month.counter == 12) {
				date = adjustDate(1, date.year.counter + 1, executionDay);
			}
			else {
				date = adjustDate(date.month.counter + 1, date.year.counter, executionDay);
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
