package database.plugin.expense.monthlyExpense;

import java.io.IOException;
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
import database.plugin.Instance;
import database.plugin.InstanceList;
import database.plugin.expense.ExpensePlugin;

public class MonthlyExpenseList extends InstanceList {
	private ExpensePlugin expensePlugin;

	public MonthlyExpenseList(ExpensePlugin expensePlugin) {
		super();
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(Map<String, String> parameter) throws IOException {
		StringBuilder builder = new StringBuilder();
		list.add(new MonthlyExpense(parameter));
		Map<String, String> parameterCopy = new HashMap<String, String>();
		parameterCopy.putAll(parameter);
		List<Map<String, String>> list = createExpense(parameterCopy, expensePlugin, ExecutionDay.getExecutionDay(parameterCopy.remove("executionday")));
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

	private List<Map<String, String>> createExpense(Map<String, String> parameter, ExpensePlugin expensePlugin, ExecutionDay executionDay) throws IOException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Date date = new Date(parameter.get("date"));
		date = adjustDate(date.month.counter, date.year.counter, executionDay);
		parameter.put("date", date.toString());
		while (date.isPast() || date.isToday()) {
			Map<String, String> parameterCopy = new HashMap<String, String>();
			parameterCopy.putAll(parameter);
			parameterCopy.remove("value");
			if (!containsParts(parameterCopy, expensePlugin.getInstanceList().getIterable())) {
				expensePlugin.create(parameter);
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

	private boolean containsParts(Map<String, String> parameter, Iterable<Instance> iterable) {
		boolean contains = false;
		for (Instance instance : iterable) {
			contains = false;
			for (Entry<String, String> entry : parameter.entrySet()) {
				if (instance.getParameter().containsKey(entry.getKey())) {
					if (instance.getParameter(entry.getKey()).equals(entry.getValue())) {
						contains = true;
					}
					else {
						contains = false;
						break;
					}
				}
			}
			if (contains) {
				return true;
			}
		}
		return false;
	}
}
