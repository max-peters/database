package database.plugin.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.InstanceList;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class EventPluginExtention extends InstancePlugin {
	public EventPluginExtention(PluginContainer pluginContainer, String identity, InstanceList instanceList, Storage storage) {
		super(pluginContainer, identity, instanceList, storage);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		map.put("date", null);
		request(map);
		instanceList.add(map);
		update();
	}
}
