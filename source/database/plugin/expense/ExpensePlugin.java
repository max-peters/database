package database.plugin.expense;

import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class ExpensePlugin extends InstancePlugin {
	public ExpensePlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "expense", new ExpenseList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]*");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]*");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void show() throws InterruptedException, NotImplementedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("show", "(all|current|average|month|day)");
		request(map);
		terminal.solutionOut(instanceList.output(map));
		graphicalUserInterface.waitForInput();
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "show":
				show();
				break;
			case "display":
				display();
				break;
		}
	}
}
