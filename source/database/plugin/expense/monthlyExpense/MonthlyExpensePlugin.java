package database.plugin.expense.monthlyExpense;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.storage.Storage;

public class MonthlyExpensePlugin extends InstancePlugin {
	public MonthlyExpensePlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("monthlyexpense", new MonthlyExpenseList(expensePlugin), storage);
	}

	@Command(tag = "edit") public void changeRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<String> strings = new ArrayList<String>();
		Map<String, String> map = new LinkedHashMap<String, String>();
		MonthlyExpense monthlyExpense = null;
		String change = null;
		int position;
		for (Instance instance : getInstanceList().getIterable()) {
			strings.add(((MonthlyExpense) instance).name + " - " + ((MonthlyExpense) instance).category);
		}
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			monthlyExpense = (MonthlyExpense) getInstanceList().get(position);
		}
		else {
			return;
		}
		strings = Arrays.asList(new String[] { "name", "category", "value" });
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			change = strings.get(position);
		}
		else {
			return;
		}
		switch (change) {
			case "name":
				map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
				break;
			case "category":
				map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
				break;
			case "value":
				map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
				break;
		}
		request(map);
		switch (change) {
			case "name":
				monthlyExpense.name = map.get("name");
				break;
			case "category":
				monthlyExpense.category = map.get("category");
				break;
			case "value":
				monthlyExpense.value = Double.valueOf(map.get("category"));
				break;
		}
		update();
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		map.put("executionday", "(first|mid|last)");
		request(map);
		create(map);
		update();
		Terminal.printCollectedLines();
	}

	@Override public void display() {
		// nothing to store here
	}

	@Override public void show() {
		// nothing to store here
	}

	@Command(tag = "stop") public void stopRequest() throws InterruptedException, BadLocationException {
		List<String> strings = new ArrayList<String>();
		for (Instance instance : getInstanceList().getIterable()) {
			strings.add(((MonthlyExpense) instance).name + " - " + ((MonthlyExpense) instance).category);
		}
		getInstanceList().remove(getInstanceList().get(Terminal.checkRequest(strings)));
	}

	@Override public void store() {
		// nothing to store here
	}
}