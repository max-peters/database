package database.plugin.expense.monthlyExpense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.date.Date;
import database.main.date.Month;
import database.main.date.Year;
import database.main.userInterface.OutputInformation;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;
import database.plugin.storage.Storage;

public class MonthlyExpensePlugin extends InstancePlugin<MonthlyExpense> {
	private ExpensePlugin expensePlugin;

	public MonthlyExpensePlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("monthlyexpense", storage, null);
		this.expensePlugin = expensePlugin;
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
		Map<String, String> map = new LinkedHashMap<String, String>();
		MonthlyExpense monthlyExpense = null;
		String change = null;
		int position;
		for (Expense expense : getIterable()) {
			strings.add(expense.name + " - " + expense.category);
		}
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			monthlyExpense = list.get(position);
		}
		else {
			return;
		}
		strings = Arrays.asList(new String[] { "name", "category", "value" });
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			change = strings.get(position);
		}
		else {
			return;
		}
		switch (change) {
			case "name":
				map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
				break;
			case "category":
				map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
				break;
			case "value":
				map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
				break;
		}
		request(map);
		switch (change) {
			case "name":
				monthlyExpense.name = map.get("name");
				break;
			case "category":
				monthlyExpense.category = map.get("category");
				break;
			case "value":
				monthlyExpense.value = Double.valueOf(map.get("category"));
				break;
		}
		update();
	}

	@Override public MonthlyExpense create(Map<String, String> parameter) {
		return new MonthlyExpense(	parameter.get("name"), parameter.get("category"), Double.valueOf(parameter.get("value")), new Date(parameter.get("date")),
									ExecutionDay.getExecutionDay(parameter.get("executionday")));
	}

	@Override public void createAndAdd(Map<String, String> parameter) {
		super.createAndAdd(parameter);
		createExpense(expensePlugin.create(parameter), expensePlugin, ExecutionDay.getExecutionDay(parameter.get("executionday")));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		map.put("executionday", "(first|mid|last)");
		request(map);
		createAndAdd(map);
		update();
		Terminal.printCollectedLines();
	}

	@Override public void display() {
		// nothing to store here
	}

	@Override public void show() {
		// nothing to store here
	}

	@Command(tag = "stop") public void stopRequest() throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
		for (Expense expense : getIterable()) {
			strings.add(expense.name + " - " + expense.category);
		}
		remove(list.get(Terminal.checkRequest(strings)));
	}

	@Override public void store() {
		// nothing to store here
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

	private boolean containsExceptValue(Iterable<Expense> iterable, Expense expense) {
		for (Expense currentExpense : iterable) {
			if (currentExpense.name.equals(expense.name) && currentExpense.category.equals(expense.category) && currentExpense.date.equals(expense.date)) {
				return true;
			}
		}
		return false;
	}

	private void createExpense(Expense expense, ExpensePlugin expensePlugin, ExecutionDay executionDay) {
		expense.date = adjustDate(expense.date.month.counter, expense.date.year.counter, executionDay);
		while (expense.date.isPast() || expense.date.isToday()) {
			if (!containsExceptValue(expensePlugin.getIterable(), expense)) {
				expensePlugin.add(expense);
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