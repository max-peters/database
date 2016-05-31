package database.plugin.monthlyExpense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.userInterface.OutputInformation;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;
import database.plugin.backup.BackupService;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;

public class MonthlyExpensePlugin extends InstancePlugin<MonthlyExpense> {
	private ExpensePlugin expensePlugin;

	public MonthlyExpensePlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("monthlyexpense", storage, null, MonthlyExpense.class);
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(MonthlyExpense monthlyExpense) {
		super.add(monthlyExpense);
		createExpense(new Expense(monthlyExpense.name, monthlyExpense.category, monthlyExpense.value, monthlyExpense.date), expensePlugin, monthlyExpense.executionDay);
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
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
		BackupService.backupChangeBefor(monthlyExpense, this);
		switch (change) {
			case "name":
				monthlyExpense.name = Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", monthlyExpense.name);
				break;
			case "category":
				monthlyExpense.category = Terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+", monthlyExpense.category);
				break;
			case "value":
				monthlyExpense.value = Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]*)?", String.valueOf(monthlyExpense.value)));
				break;
		}
		BackupService.backupChangeAfter(monthlyExpense, this);
		Terminal.update();
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, NumberFormatException {
		MonthlyExpense monthlyExpense = new MonthlyExpense(	Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+"), Terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+"),
															Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?")),
															LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu")),
															ExecutionDay.getExecutionDay(Terminal.request("executionday", "(first|mid|last)")));
		add(monthlyExpense);
		BackupService.backupCreation(monthlyExpense, this);
		Terminal.update();
	}

	@Override public void display() {
		// nothing to display here
	}

	@Override public void show() {
		// nothing to show here
	}

	@Command(tag = "stop") public void stopRequest() throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
		for (Expense expense : getIterable()) {
			strings.add(expense.name + " - " + expense.category);
		}
		int position = Terminal.checkRequest(strings);
		if (position < 0) {
			return;
		}
		MonthlyExpense monthlyExpense = list.get(position);
		remove(monthlyExpense);
		BackupService.backupRemoval(monthlyExpense, this);
		Terminal.update();
	}

	@Override public void store() {
		// nothing to store here
	}

	private LocalDate adjustDate(int month, int year, ExecutionDay executionDay) {
		LocalDate date = LocalDate.parse(Terminal.parseDateString("01." + month + "." + year), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		switch (executionDay) {
			case FIRST:
				break;
			case LAST:
				date = date.withDayOfMonth(date.lengthOfMonth());
				break;
			case MID:
				date = date.withDayOfMonth(date.lengthOfMonth() / 2);
				break;
		}
		return date;
	}

	private boolean equalsExceptValue(Iterable<Expense> iterable, Expense expense) {
		for (Expense currentExpense : iterable) {
			if (currentExpense.name.equals(expense.name) && currentExpense.category.equals(expense.category) && currentExpense.date.isEqual(expense.date)) {
				return true;
			}
		}
		return false;
	}

	private void createExpense(Expense expense, ExpensePlugin expensePlugin, ExecutionDay executionDay) {
		expense.date = adjustDate(expense.date.getMonthValue(), expense.date.getYear(), executionDay);
		while (expense.date.isBefore(LocalDate.now()) || expense.date.isEqual(LocalDate.now())) {
			if (!equalsExceptValue(expensePlugin.getIterable(), expense)) {
				expensePlugin.add(new Expense(expense.name, expense.category, expense.value, expense.date));
				if (!Terminal.getCollectedLines().contains(new OutputInformation("expense", StringType.SOLUTION, StringFormat.BOLD))) {
					Terminal.collectLine("expense", StringFormat.BOLD);
				}
				Terminal.collectLine(" + "+ expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " + expense.name + " (" + expense.category + ") " + expense.value
										+ "€", StringFormat.STANDARD);
			}
			if (expense.date.getMonthValue() == 12) {
				expense.date = adjustDate(1, expense.date.getYear() + 1, executionDay);
			}
			else {
				expense.date = adjustDate(expense.date.getMonthValue() + 1, expense.date.getYear(), executionDay);
			}
		}
	}
}