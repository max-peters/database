package database.plugin.refilling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.ExpenseList;

public class RefillingPlugin extends InstancePlugin {
	public RefillingPlugin(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration, ExpenseList expenseList) {
		super(pluginContainer, graphicalUserInterface, administration, "refilling", new RefillingList(expenseList));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("distance", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void showRequest() throws InterruptedException {
		Terminal.solutionOut(instanceList.output(null));
		graphicalUserInterface.waitForInput();
	}
}
