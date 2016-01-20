package database.plugin.refilling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.storage.Storage;

public class RefillingPlugin extends InstancePlugin {
	public RefillingPlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("refilling", new RefillingList(expensePlugin), storage);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("distance", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}
}
