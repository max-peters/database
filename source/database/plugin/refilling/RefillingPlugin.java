package database.plugin.refilling;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;
import database.plugin.backup.BackupService;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;

public class RefillingPlugin extends InstancePlugin<Refilling> {
	private ExpensePlugin expensePlugin;

	public RefillingPlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("refilling", storage, new RefillingOutputFormatter(), Refilling.class);
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(Refilling refilling) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(refilling.date)) {
			i--;
		}
		list.add(i, refilling);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		Refilling refilling = new Refilling(Double.valueOf(Terminal.request("distance", "[0-9]{1,13}(\\.[0-9]*)?")),
											Double.valueOf(Terminal.request("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?")),
											Double.valueOf(Terminal.request("cost", "[0-9]{1,13}(\\.[0-9]*)?")),
											LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu")));
		Expense expense = new Expense("Auto - Tankstelle", "Fahrtkosten", refilling.cost, refilling.date);
		add(refilling);
		expensePlugin.add(expense);
		BackupService.backupCreation(refilling, this);
		BackupService.backupRelatedCreation(expense, expensePlugin);
		Terminal.update();
	}
}
