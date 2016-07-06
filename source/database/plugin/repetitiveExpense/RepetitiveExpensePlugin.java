package database.plugin.repetitiveExpense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.StringFormat;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.FormatterProvider;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;

public class RepetitiveExpensePlugin extends InstancePlugin<RepetitiveExpense> {
	public RepetitiveExpensePlugin() {
		super("repetitiveexpense", RepetitiveExpense.class);
	}

	@Override public void add(RepetitiveExpense repetitiveExpense) {
		super.add(repetitiveExpense);
	}

	public void createExpense(ITerminal terminal, ExpensePlugin expensePlugin, BackupService backupService) {
		for (RepetitiveExpense repetitiveExpense : list) {
			createExpense(repetitiveExpense, terminal, expensePlugin, backupService);
		}
	}

	@Command(tag = "new") public void createRequest(ITerminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws InterruptedException, BadLocationException, NumberFormatException {
		String name = terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		String category = terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		Double value = Double.valueOf(terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		ExecutionDay executionDay = ExecutionDay.getExecutionDay(terminal.request("executionday", "(first|mid|last)"));
		int interval = Integer.valueOf(terminal.request("interval", "[0-9]{1,13}"));
		RepetitiveExpense repetitiveExpense = new RepetitiveExpense(name, category, value, date, executionDay, interval);
		add(repetitiveExpense);
		backupService.backupCreation(repetitiveExpense, this);
		createExpense(repetitiveExpense, terminal, (ExpensePlugin) pluginContainer.getPlugin("expense"), backupService);
		terminal.update(pluginContainer, formatterProvider);
	}

	@Override public void display(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) {
		// nothing to display here
	}

	@Override public void show(ITerminal terminal, FormatterProvider formatterProvider) {
		// nothing to show here
	}

	@Command(tag = "stop") public void stopRequest(	ITerminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
		for (Expense expense : getIterable()) {
			strings.add(expense.name + " - " + expense.category);
		}
		int position = terminal.checkRequest(strings);
		if (position < 0) {
			return;
		}
		RepetitiveExpense repetitiveExpense = list.get(position);
		remove(repetitiveExpense);
		backupService.backupRemoval(repetitiveExpense, this);
		terminal.update(pluginContainer, formatterProvider);
	}

	@Override public void store(PluginContainer pluginContainer, ITerminal terminal, FormatterProvider formatterProvider) {
		// nothing to store here
	}

	private LocalDate adjustDate(int month, int year, ExecutionDay executionDay) {
		LocalDate date = LocalDate.of(year, month, 1);
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

	private void createExpense(RepetitiveExpense repetitiveExpense, ITerminal terminal, ExpensePlugin expensePlugin, BackupService backupService) {
		Expense expense = new Expense(repetitiveExpense.name, repetitiveExpense.category, repetitiveExpense.value, repetitiveExpense.date);
		expense.date = adjustDate(expense.date.getMonthValue(), expense.date.getYear(), repetitiveExpense.executionDay);
		while (expense.date.isBefore(LocalDate.now()) || expense.date.isEqual(LocalDate.now())) {
			if (!equalsExceptValue(expensePlugin.getIterable(), expense)
				&& Math.floorMod(ChronoUnit.MONTHS.between(repetitiveExpense.date, expense.date), repetitiveExpense.interval) == 0) {
				Expense newExpense = new Expense(expense.name, expense.category, expense.value, expense.date);
				expensePlugin.add(newExpense);
				backupService.backupRelatedCreation(newExpense, expensePlugin);
				terminal.collectLine(" + "+ expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " + expense.name + " (" + expense.category + ") " + expense.value
										+ "€", StringFormat.STANDARD, "expense");
			}
			if (expense.date.getMonthValue() == 12) {
				expense.date = adjustDate(1, expense.date.getYear() + 1, repetitiveExpense.executionDay);
			}
			else {
				expense.date = adjustDate(expense.date.getMonthValue() + 1, expense.date.getYear(), repetitiveExpense.executionDay);
			}
		}
	}

	private boolean equalsExceptValue(Iterable<Expense> iterable, Expense expense) {
		for (Expense currentExpense : iterable) {
			if (currentExpense.name.equals(expense.name) && currentExpense.category.equals(expense.category) && currentExpense.date.isEqual(expense.date)) {
				return true;
			}
		}
		return false;
	}
}