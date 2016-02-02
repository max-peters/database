package database.plugin.expense.monthlyExpense;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import database.main.date.Date;
import database.main.date.Month;
import database.main.date.Year;
import database.main.userInterface.OutputInformation;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
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
		list.add(new MonthlyExpense(parameter));
		createExpense(new Expense(parameter), expensePlugin, ExecutionDay.getExecutionDay(parameter.get("executionday")));
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

	private void createExpense(Expense expense, ExpensePlugin expensePlugin, ExecutionDay executionDay)	throws IOException, InstantiationException, IllegalAccessException,
																										IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
																										SecurityException {
		expense.date = adjustDate(expense.date.month.counter, expense.date.year.counter, executionDay);
		while (expense.date.isPast() || expense.date.isToday()) {
			if (!containsExceptValue(expensePlugin.getInstanceList().getIterable(), expense)) {
				expensePlugin.create(expense.getParameter());
				if (!Terminal.getCollectedLines().contains(new OutputInformation("expense created:", StringType.SOLUTION, StringFormat.STANDARD))) {
					Terminal.collectLine("expense created:", StringFormat.STANDARD);
				}
				Terminal.collectLine(" - " + expense.date + " " + expense.name + " (" + expense.category + ") " + expense.value + "€", StringFormat.STANDARD);
			}
			if (expense.date.month.counter == 12) {
				expense.date = adjustDate(1, expense.date.year.counter + 1, executionDay);
			}
			else {
				expense.date = adjustDate(expense.date.month.counter + 1, expense.date.year.counter, executionDay);
			}
		}
	}
}
