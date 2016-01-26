package database.plugin.expense.monthlyExpense;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.storage.Storage;

public class MonthlyExpensePlugin extends InstancePlugin {
	public MonthlyExpensePlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("monthlyexpense", new MonthlyExpenseList(expensePlugin), storage);
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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