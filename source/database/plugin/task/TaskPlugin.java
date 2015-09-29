package database.plugin.task;

import java.util.HashMap;
import java.util.Map;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class TaskPlugin extends InstancePlugin {
	public TaskPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "task", new TaskList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", ".+");
		request(map);
		create(map);
		update();
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "display":
				display();
				break;
		}
	}
}