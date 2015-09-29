package database.plugin.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstanceList;
import database.plugin.InstancePlugin;

public abstract class EventExtention extends InstancePlugin {
	public EventExtention(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, identity, instanceList);
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
			map.put("date", null);
			request(map);
			instanceList.add(map);
			update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Override public void conduct(String command) throws InterruptedException {
	}

	@Getter public InstanceList getInstanceList() {
		return instanceList;
	}
}
