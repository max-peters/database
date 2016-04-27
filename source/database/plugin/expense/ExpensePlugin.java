package database.plugin.expense;

import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.userInterface.Terminal;
import database.main.userInterface.autocompletion.Autocompletion;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class ExpensePlugin extends InstancePlugin<Expense> {
	public ExpensePlugin(Storage storage, Backup backup) {
		super("expense", storage, new ExpenseOutputFormatter(), backup);
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.compareTo(expense.date) > 0) {
			i--;
		}
		list.add(i, expense);
	}

	@Override public Expense create(Map<String, String> parameter) {
		return new Expense(parameter.get("name"), parameter.get("category"), Double.valueOf(parameter.get("value")), new Date(parameter.get("date")));
	}

	@Override public Expense create(NamedNodeMap nodeMap) {
		return new Expense(	nodeMap.getNamedItem("name").getNodeValue(), nodeMap.getNamedItem("category").getNodeValue(),
							Double.valueOf(nodeMap.getNamedItem("value").getNodeValue()), new Date(nodeMap.getNamedItem("date").getNodeValue()));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		ExpenseOutputFormatter formatter = (ExpenseOutputFormatter) this.formatter;
		String name;
		name = formatter.getNameByString(Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", new Autocompletion((String input) -> {
			return formatter.getMostUsedNameByPrefix(input, list);
		})), list);
		add(new Expense(name, formatter.getCategoryByString(Terminal.request(	"category", "[A-ZÖÄÜa-zöäüß\\- ]+", formatter.getMostUsedCategoryByPrefixAndName("", name, list),
																				new Autocompletion((String input) -> {
																					return formatter.getMostUsedCategoryByPrefixAndName(input, "", list);
																				})),
															list),
						Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?")), new Date(Terminal.request("date", "DATE", Date.getCurrentDate().toString()))));
		update();
	}
}
