package database.plugin.expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.FormatterProvider;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;

public class ExpensePlugin extends InstancePlugin<Expense> {
	public ExpensePlugin() {
		super("expense", Expense.class);
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(expense.date)) {
			i--;
		}
		list.add(i, expense);
	}

	@Command(tag = "new") public void createRequest(ITerminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws InterruptedException, BadLocationException {
		ExpenseOutputFormatter formatter = (ExpenseOutputFormatter) formatterProvider.getFormatter(Expense.class);
		String name = formatter.getNameByString(terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", (String input) -> {
			return formatter.getMostUsedNameByPrefix(input, list);
		}), list);
		String category = formatter.getCategoryByString(terminal.request(	"category", "[A-ZÖÄÜa-zöäüß\\- ]+", formatter.getMostUsedCategoryByPrefixAndName("", name, list),
																			(String input) -> {
																				return formatter.getMostUsedCategoryByPrefixAndName(input, "", list);
																			}),
														list);
		Double value = Double.valueOf(terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		String temp = terminal.request("date", "DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Expense expense = new Expense(name, category, value, date);
		add(expense);
		formatter.addExpense(expense);
		backupService.backupCreation(expense, this);
		if (display) {
			terminal.update(pluginContainer, formatterProvider);
		}
	}
}
