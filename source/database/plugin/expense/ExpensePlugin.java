package database.plugin.expense;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.UserCancelException;
import database.main.autocompletition.IAutocomplete;
import database.main.autocompletition.ResultSetAutocomplete;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;
import database.services.database.IDatabase;

public class ExpensePlugin extends InstancePlugin<Expense> {
	private IAutocomplete	categoryAutocomplete;
	private IAutocomplete	nameAutocomplete;

	public ExpensePlugin(IDatabase database) throws SQLException {
		super("expense", new ExpenseOutputFormatter(), Expense.class);
		nameAutocomplete = new ResultSetAutocomplete(database.execute(ExpenseSQLStatements.AUTOCOMPLETE_NAME));
		categoryAutocomplete = new ResultSetAutocomplete(database.execute(ExpenseSQLStatements.AUTOCOMPLETE_CATEGORY));
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(expense.date)) {
			i--;
		}
		list.add(i, expense);
	}

	@Command(tag = "new") public void createRequest(ITerminal terminal, BackupService backupService,
													PluginContainer pluginContainer) throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		String name = getNameByString(terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", nameAutocomplete));
		String category = getCategoryByString(terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+", categoryAutocomplete.getMostUsedString("", name), categoryAutocomplete));
		Double value = Double.valueOf(terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		String temp = terminal.request("date", "DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Expense expense = new Expense(name, category, value, date);
		add(expense);
		backupService.backupCreation(expense, this);
		if (display) {
			terminal.update(pluginContainer);
		}
	}

	private String getCategoryByString(String name) {
		for (Expense expense : list) {
			if (expense.category.equalsIgnoreCase(name)) {
				return expense.category;
			}
		}
		return name;
	}

	private String getNameByString(String name) {
		for (Expense expense : list) {
			if (expense.name.equalsIgnoreCase(name)) {
				return expense.name;
			}
		}
		return name;
	}
}
