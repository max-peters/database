package database.plugin.expense;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.PrintInformation;
import database.plugin.expense.monthlyExpense.MonthlyExpensePlugin;
import database.plugin.storage.Storage;

public class ExpensePlugin extends InstancePlugin<Expense> {
	private MonthlyExpensePlugin monthlyExpensePlugin;

	public ExpensePlugin(Storage storage) {
		super("expense", new ExpenseList(), storage);
		monthlyExpensePlugin = new MonthlyExpensePlugin(this, storage);
	}

	@Override public void clearList() {
		super.clearList();
		monthlyExpensePlugin.clearList();
	}

	@Override public Expense create(Map<String, String> map) {
		return new Expense(map);
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (Instance instance : getInstanceList()) {
			list.add(new PrintInformation("expense", instance.getParameter()));
		}
		for (Instance instance : monthlyExpensePlugin.getInstanceList()) {
			list.add(new PrintInformation("monthlyexpense", instance.getParameter()));
		}
		list.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void read(PrintInformation pair)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException {
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
}
