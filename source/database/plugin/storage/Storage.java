package database.plugin.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Pair;
import database.plugin.Plugin;

public class Storage extends Plugin {
	private ArrayList<String>	storage;
	private PluginContainer		pluginContainer;

	public Storage(PluginContainer pluginContainer, Administration administration, GraphicalUserInterface graphicalUserInterface) {
		super("storage", administration, graphicalUserInterface);
		this.pluginContainer = pluginContainer;
		this.storage = new ArrayList<String>();
	}

	public ArrayList<String> getStorage() {
		return storage;
	}

	@Override public void conduct(String command) throws InterruptedException, IOException {
		store();
	}

	@Command(tag = "store") public void store() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("store", pluginContainer.getPluginNameTagsAsRegesx());
		request(map);
		Plugin plugin = pluginContainer.getPlugin(map.get("store"));
		InstancePlugin instancePlugin;
		String parameterString;
		if (plugin != null && plugin instanceof InstancePlugin) {
			instancePlugin = (InstancePlugin) plugin;
		}
		else {
			administration.errorMessage();
			return;
		}
		for (Instance instance : instancePlugin.getList()) {
			parameterString = "";
			for (Entry<String, String> entry : instance.getParameter().entrySet()) {
				parameterString = parameterString + entry.getKey() + ": " + entry.getValue() + ", ";
			}
			storage.add(parameterString.substring(0, parameterString.lastIndexOf(',')));
		}
		instancePlugin.getList().clear();
		instancePlugin.update();
	}

	@Override public List<Pair> getPairList() {
		List<Pair> list = new ArrayList<Pair>();
		for (String string : storage) {
			Pair pair = new Pair("entry");
			pair.put("string", string);
			list.add(pair);
		}
		return list;
	}

	@Override public void create(Pair pair) {
		storage.addAll(pair.getMap().values());
	}
}
