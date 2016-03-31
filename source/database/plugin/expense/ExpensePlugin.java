package database.plugin.expense;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import database.main.date.Date;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.monthlyExpense.MonthlyExpensePlugin;
import database.plugin.storage.Storage;

public class ExpensePlugin extends InstancePlugin<Expense> {
	private MonthlyExpensePlugin monthlyExpensePlugin;

	public ExpensePlugin(Storage storage) {
		super("expense", storage, new ExpenseOutputFormatter());
		monthlyExpensePlugin = new MonthlyExpensePlugin(this, storage);
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.compareTo(expense.date) > 0) {
			i--;
		}
		list.add(i, expense);
	}

	@Override public void clearList() {
		super.clearList();
		monthlyExpensePlugin.clearList();
	}

	@Override public Expense create(Map<String, String> map) {
		return new Expense(map.get("name"), map.get("category"), Double.valueOf(map.get("value")), new Date(map.get("date")));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]{0,2})?");
		map.put("date", null);
		request(map);
		createAndAdd(map);
		update();
	}

	@Command(tag = "monthly") public void monthlyRequest()	throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException,
															BadLocationException {
		monthlyExpensePlugin.conduct(Terminal.request("monthly expense", monthlyExpensePlugin.getCommandTags(monthlyExpensePlugin.getClass())));
	}

	@Override public void print(Document document, Element element) {
		for (Expense expense : getIterable()) {
			Element entryElement = document.createElement("expense");
			expense.insertParameter(entryElement);
			element.appendChild(entryElement);
		}
		for (Expense expense : monthlyExpensePlugin.getIterable()) {
			Element entryElement = document.createElement("monthlyexpense");
			expense.insertParameter(entryElement);
			element.appendChild(entryElement);
		}
		Element entryElement = document.createElement("display");
		entryElement.setAttribute("boolean", String.valueOf(getDisplay()));
		element.appendChild(entryElement);
	}

	@Override public void read(String nodeName, Map<String, String> parameter) {
		if (nodeName.equals("display")) {
			setDisplay(Boolean.valueOf(parameter.get("boolean")));
		}
		else if (nodeName.equals("expense")) {
			createAndAdd(parameter);
		}
		else if (nodeName.equals("monthlyexpense")) {
			monthlyExpensePlugin.createAndAdd(parameter);
		}
	}
}
