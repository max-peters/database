package database.plugin.expense.monthlyExpense;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import database.main.date.Date;
import database.main.date.Month;
import database.main.date.Year;
import database.main.userInterface.StringFormat;
import database.main.userInterface.Terminal;
import database.plugin.Instance;
import database.plugin.InstanceList;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;

public class MonthlyExpenseList extends InstanceList {
	private ExpensePlugin expensePlugin;

	public MonthlyExpenseList(ExpensePlugin expensePlugin) {
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(Map<String, String> parameter)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
																InvocationTargetException, NoSuchMethodException, SecurityException {
		StringBuilder builder = new StringBuilder();
		list.add(new MonthlyExpense(parameter));
		List<Expense> list = createExpense(new Expense(parameter), expensePlugin, ExecutionDay.getExecutionDay(parameter.get("executionday")));
		for (Expense expense : list) {
			builder.append(" - " + expense.date + " " + expense.name + " (" + expense.category + ") " + expense.value + "€");
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

	private boolean containsExceptValue(Iterable<? extends Instance> iterable, Expense expense) {
		for (Instance instance : iterable) {
			Expense currentExpense = (Expense) instance;
			if (currentExpense.name.equals(expense.name) && currentExpense.category.equals(expense.category) && currentExpense.date.equals(expense.date)) {
				return true;
			}
		}
		return false;
	}

	private List<Expense> createExpense(Expense expense, ExpensePlugin expensePlugin, ExecutionDay executionDay)	throws IOException, InstantiationException, IllegalAccessException,
																													IllegalArgumentException, InvocationTargetException,
																													NoSuchMethodException, SecurityException {
		List<Expense> list = new ArrayList<Expense>();
		expense.date = adjustDate(expense.date.month.counter, expense.date.year.counter, executionDay);
		while (expense.date.isPast() || expense.date.isToday()) {
			if (!containsExceptValue(expensePlugin.getInstanceList().getIterable(), expense)) {
				expensePlugin.create(expense.getParameter());
				list.add(new Expense(expense.getParameter()));
			}
			if (expense.date.month.counter == 12) {
				expense.date = adjustDate(1, expense.date.year.counter + 1, executionDay);
			}
			else {
				expense.date = adjustDate(expense.date.month.counter + 1, expense.date.year.counter, executionDay);
			}
		}
		return list;
	}
}
