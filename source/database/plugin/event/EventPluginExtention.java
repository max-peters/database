package database.plugin.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.InstanceList;
import database.plugin.InstancePlugin;

public class EventPluginExtention extends InstancePlugin {
	public EventPluginExtention(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		super(pluginContainer, graphicalUserInterface, administration, identity, instanceList);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		map.put("date", null);
		request(map);
		instanceList.add(map);
		update();
	}
}
