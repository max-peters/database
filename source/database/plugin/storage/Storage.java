package database.plugin.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.RequestInformation;

public class Storage extends Plugin {
	private PluginContainer		pluginContainer;
	private ArrayList<String>	storage;

	public Storage(PluginContainer pluginContainer) {
		super("storage");
		this.pluginContainer = pluginContainer;
		storage = new ArrayList<String>();
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

	public ArrayList<String> getStorage() {
		return storage;
	}

	public void clearList() {
		storage.clear();
	}

	@Override public void readInformation(RequestInformation pair) {
		storage.addAll(pair.getMap().values());
	}

	@Command(tag = "store") public void storeRequest() throws InterruptedException, BadLocationException {
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
			Terminal.errorMessage();
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
}
