package database.plugin.expense;

import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.userInterface.Terminal;
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
		final String name;
		String temp = null;
		String category;
		Double value;
		Date date;
		Thread thread = new Thread(() -> {
			while (true) {
				try {
					Terminal.setInputText(formatter.getMostUsedNameByPrefix(Terminal.readKey(), list));
				}
				catch (InterruptedException e) {
					break;
				}
			}
		});
		thread.start();
		try {
			temp = formatter.getNameByString(Terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+"), list);
		}
		catch (CancellationException e) {
			thread.interrupt();
			throw new CancellationException();
		}
		name = temp;
		thread.interrupt();
		thread = new Thread(() -> {
			Terminal.setInputText(formatter.getMostUsedCategoryByPrefixAndName("", name, list));
			while (true) {
				try {
					Terminal.setInputText(formatter.getMostUsedCategoryByPrefixAndName(Terminal.readKey(), "", list));
				}
				catch (InterruptedException e) {
					break;
				}
			}
		});
		thread.start();
		try {
			category = formatter.getCategoryByString(Terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+"), list);
		}
		catch (CancellationException e) {
			thread.interrupt();
			throw new CancellationException();
		}
		thread.interrupt();
		value = Double.valueOf(Terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		date = new Date(Terminal.request("date", "DATE", Date.getCurrentDate().toString()));
		add(new Expense(name, category, value, date));
		update();
	}
}
