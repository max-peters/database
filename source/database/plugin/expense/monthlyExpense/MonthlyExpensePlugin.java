package database.plugin.expense.monthlyExpense;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.ExpenseList;
import database.plugin.storage.Storage;

public class MonthlyExpensePlugin extends InstancePlugin {
	public MonthlyExpensePlugin(PluginContainer pluginContainer, ExpenseList expenseList, Storage storage) {
		super(pluginContainer, "monthlyexpense", new MonthlyExpenseList(expenseList), storage);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}
}