package database.plugin.repetitiveExpense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

public class RepetitiveExpensePlugin extends InstancePlugin<RepetitiveExpense> {
	private ExpensePlugin expensePlugin;

	public RepetitiveExpensePlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("repetitiveexpense", storage, null, RepetitiveExpense.class);
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(RepetitiveExpense repetitiveExpense) {
		super.add(repetitiveExpense);
		createExpense(repetitiveExpense, expensePlugin);
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
		RepetitiveExpense repetitiveExpense = null;
		String change = null;
		int position;
		for (Expense expense : getIterable()) {
			strings.add(expense.name + " - " + expense.category);
		}
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			repetitiveExpense = list.get(position);
		}
		else {
			return;
		}
		strings = Arrays.asList(new String[] { "name", "category", "value", "interval" });
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			change = strings.get(position);
		}
		else {
			return;
		}
		BackupService.backupChangeBefor(repetitiveExpense, this);
		switch (change) {
			case "name":
				repetitiveExpense.name = Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", repetitiveExpense.name);
				break;
			case "category":
				repetitiveExpense.category = Terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+", repetitiveExpense.category);
				break;
			case "value":
				repetitiveExpense.value = Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]*)?", String.valueOf(repetitiveExpense.value)));
				break;
			case "interval":
				repetitiveExpense.interval = Integer.valueOf(Terminal.request("interval", "[0-9]{1,13}", String.valueOf(repetitiveExpense.interval)));
				break;
		}
		BackupService.backupChangeAfter(repetitiveExpense, this);
		Terminal.update();
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, NumberFormatException {
		String name = Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		String category = Terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		Double value = Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		String temp = Terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		ExecutionDay executionDay = ExecutionDay.getExecutionDay(Terminal.request("executionday", "(first|mid|last)"));
		int interval = Integer.valueOf(Terminal.request("interval", "[0-9]{1,13}"));
		RepetitiveExpense repetitiveExpense = new RepetitiveExpense(name, category, value, date, executionDay, interval);
		add(repetitiveExpense);
		BackupService.backupCreation(repetitiveExpense, this);
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
		RepetitiveExpense repetitiveExpense = list.get(position);
		remove(repetitiveExpense);
		BackupService.backupRemoval(repetitiveExpense, this);
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

	private void createExpense(RepetitiveExpense repetitiveExpense, ExpensePlugin expensePlugin) {
		Expense expense = new Expense(repetitiveExpense.name, repetitiveExpense.category, repetitiveExpense.value, repetitiveExpense.date);
		expense.date = adjustDate(expense.date.getMonthValue(), expense.date.getYear(), repetitiveExpense.executionDay);
		while (expense.date.isBefore(LocalDate.now()) || expense.date.isEqual(LocalDate.now())) {
			if (!equalsExceptValue(expensePlugin.getIterable(), expense)
				&& Math.floorMod(ChronoUnit.MONTHS.between(repetitiveExpense.date, expense.date), repetitiveExpense.interval) == 0) {
				expensePlugin.add(new Expense(expense.name, expense.category, expense.value, expense.date));
				if (!Terminal.getCollectedLines().contains(new OutputInformation("expense", StringType.SOLUTION, StringFormat.BOLD))) {
					Terminal.collectLine("expense", StringFormat.BOLD);
				}
				Terminal.collectLine(" + "+ expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " + expense.name + " (" + expense.category + ") " + expense.value
										+ "€", StringFormat.STANDARD);
			}
			if (expense.date.getMonthValue() == 12) {
				expense.date = adjustDate(1, expense.date.getYear() + 1, repetitiveExpense.executionDay);
			}
			else {
				expense.date = adjustDate(expense.date.getMonthValue() + 1, expense.date.getYear(), repetitiveExpense.executionDay);
			}
		}
	}
}