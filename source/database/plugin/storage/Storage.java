package database.plugin.storage;

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
import database.plugin.Plugin;
import database.plugin.RequestInformation;

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

	@Command(tag = "store") public void storeRequest() throws InterruptedException {
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

	@Override public List<RequestInformation> getInformationList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		for (String string : storage) {
			RequestInformation pair = new RequestInformation("entry");
			pair.put("string", string);
			list.add(pair);
		}
		return list;
	}

	@Override public void readInformation(RequestInformation pair) {
		storage.addAll(pair.getMap().values());
	}
}
