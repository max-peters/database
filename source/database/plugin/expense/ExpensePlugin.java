package database.plugin.expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.main.userInterface.autocompletion.Autocompletion;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;
import database.plugin.backup.BackupService;

public class ExpensePlugin extends InstancePlugin<Expense> {
	public ExpensePlugin(Storage storage) {
		super("expense", storage, new ExpenseOutputFormatter(), Expense.class);
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(expense.date)) {
			i--;
		}
		list.add(i, expense);
		((ExpenseOutputFormatter) formatter).addExpense(expense, list);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		ExpenseOutputFormatter formatter = (ExpenseOutputFormatter) this.formatter;
		String name = formatter.getNameByString(Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", new Autocompletion((String input) -> {
			return formatter.getMostUsedNameByPrefix(input, list);
		})), list);
		String category = formatter.getCategoryByString(Terminal.request(	"category", "[A-ZÖÄÜa-zöäüß\\- ]+", formatter.getMostUsedCategoryByPrefixAndName("", name, list),
																			new Autocompletion((String input) -> {
																				return formatter.getMostUsedCategoryByPrefixAndName(input, "", list);
																			})),
														list);
		Double value = Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		String temp = Terminal.request("date", "DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Expense expense = new Expense(name, category, value, date);
		add(expense);
		BackupService.backupCreation(expense, this);
		Terminal.update();
	}
}
