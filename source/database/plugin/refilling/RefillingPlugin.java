package database.plugin.refilling;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.storage.Storage;

public class RefillingPlugin extends InstancePlugin<Refilling> {
	private ExpensePlugin expensePlugin;

	public RefillingPlugin(ExpensePlugin expensePlugin, Storage storage) {
		super("refilling", storage, new RefillingOutputFormatter());
		this.expensePlugin = expensePlugin;
	}

	@Override public Refilling create(Map<String, String> map) {
		return new Refilling(map);
	}

	@Override public void createAndAdd(Map<String, String> map) {
		super.createAndAdd(map);
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("name", "Auto - Tankstelle");
		parameter.put("category", "Fahrtkosten");
		parameter.put("value", map.get("cost"));
		parameter.put("date", map.get("date"));
		expensePlugin.createAndAdd(parameter);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("cost", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("distance", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		createAndAdd(map);
		update();
	}
}
