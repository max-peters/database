package database.plugin.refilling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.FormatterProvider;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;

public class RefillingPlugin extends InstancePlugin<Refilling> {
	public RefillingPlugin() {
		super("refilling", Refilling.class);
	}

	@Override public void add(Refilling refilling) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(refilling.date)) {
			i--;
		}
		list.add(i, refilling);
	}

	@Command(tag = "new") public void createRequest(ITerminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws InterruptedException, BadLocationException {
		Double distance = Double.valueOf(terminal.request("distance", "[0-9]{1,13}(\\.[0-9]*)?"));
		Double refuelAmount = Double.valueOf(terminal.request("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?"));
		Double cost = Double.valueOf(terminal.request("cost", "[0-9]{1,13}(\\.[0-9]*)?"));
		ExpensePlugin expensePlugin = (ExpensePlugin) pluginContainer.getPlugin("expense");
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Refilling refilling = new Refilling(distance, refuelAmount, cost, date);
		Expense expense = new Expense("Tankstelle", "Fahrtkosten", refilling.cost, refilling.date);
		add(refilling);
		expensePlugin.add(expense);
		backupService.backupCreation(refilling, this);
		backupService.backupRelatedCreation(expense, expensePlugin);
		terminal.update(pluginContainer, formatterProvider);
	}
}
