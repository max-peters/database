package database.plugin.expense;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.PrintInformation;
import database.plugin.expense.monthlyExpense.MonthlyExpensePlugin;
import database.plugin.storage.Storage;

public class ExpensePlugin extends InstancePlugin<Expense> {
	private MonthlyExpensePlugin monthlyExpensePlugin;

	public ExpensePlugin(Storage storage) {
		super("expense", storage, new ExpenseOutputFormatter());
		monthlyExpensePlugin = new MonthlyExpensePlugin(this, storage);
	}

	@Override public void clearList() {
		super.clearList();
		monthlyExpensePlugin.clearList();
	}

	@Override public Expense create(Map<String, String> map) {
		return new Expense(map);
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

	@Override public List<PrintInformation> print() {
		List<PrintInformation> printInformationList = new ArrayList<PrintInformation>();
		for (Expense expense : getIterable()) {
			printInformationList.add(new PrintInformation("expense", expense.getParameter()));
		}
		for (Expense expense : monthlyExpensePlugin.getIterable()) {
			printInformationList.add(new PrintInformation("monthlyexpense", expense.getParameter()));
		}
		printInformationList.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return printInformationList;
	}

	@Override public void read(PrintInformation pair) {
		if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else if (pair.getName().equals("expense")) {
			createAndAdd(pair.getMap());
		}
		else if (pair.getName().equals("monthlyexpense")) {
			monthlyExpensePlugin.createAndAdd(pair.getMap());
		}
	}

	@Override public void add(Expense expense) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.compareTo(expense.date) > 0) {
			i--;
		}
		list.add(i, expense);
	}
}
