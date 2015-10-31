package database.plugin.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class TaskPlugin extends InstancePlugin {
	public TaskPlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "task", new TaskList());
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", ".+");
		request(map);
		create(map);
		update();
	}
}