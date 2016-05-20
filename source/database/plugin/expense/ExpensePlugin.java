package database.plugin.expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;
import database.main.userInterface.autocompletion.Autocompletion;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;

public class ExpensePlugin extends InstancePlugin<Expense> {
	public ExpensePlugin(Storage storage, Backup backup) {
		super("expense", storage, new ExpenseOutputFormatter(), backup);
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(expense.date)) {
			i--;
		}
		list.add(i, expense);
		((ExpenseOutputFormatter) formatter).addExpense(expense, list);
	}

	@Override public Expense create(NamedNodeMap nodeMap) {
		return new Expense(	nodeMap.getNamedItem("name").getNodeValue(), nodeMap.getNamedItem("category").getNodeValue(),
							Double.valueOf(nodeMap.getNamedItem("value").getNodeValue()),
							LocalDate.parse(nodeMap.getNamedItem("date").getNodeValue(), DateTimeFormatter.ofPattern("dd.MM.uuuu")));
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
		LocalDate date = LocalDate.parse(	Terminal.request("date", "DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.mm.yyyy"))),
											DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		backup.backup();
		add(new Expense(name, category, value, date));
		Terminal.update();
	}
}
