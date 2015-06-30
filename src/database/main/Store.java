package database.main;

import java.util.ArrayList;
import java.util.List;

public class Store {
	private ArrayList<Plugin>	plugins;
	private ArrayList<String>	storage;
	private boolean				changes;

	public Store() {
		this.plugins = new ArrayList<Plugin>();
		this.storage = new ArrayList<String>();
		this.changes = false;
	}

	public void addPlugin(Plugin plugin) {
		this.plugins.add(plugin);
	}

	public void addPlugin(List<Plugin> plugins) {
		this.plugins.addAll(plugins);
	}

	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	public ArrayList<String> getListToWrite() {
		ArrayList<String> listToWrite = new ArrayList<String>();
		for (Plugin plugin : plugins) {
			for (Object object : plugin.instanceList.list) {
				listToWrite.add(object.toString());
			}
		}
		for (Plugin plugin : plugins) {
			listToWrite.add("boolean : " + plugin.identity + " / " + plugin.getDisplay());
		}
		return listToWrite;
	}

	public void addToStorage(String toAdd) {
		storage.add(toAdd);
	}

	public ArrayList<String> getStorage() {
		return storage;
	}

	public void setChanges(boolean changes) {
		this.changes = changes;
	}

	public boolean getChanges() {
		return changes;
	}

	public Plugin getPlugin(String identity) {
		for (Plugin plugin : plugins) {
			if (plugin.identity.equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	public String getTagsAsRegex() {
		String regex = "(";
		for (Plugin plugin : plugins) {
			regex = regex + plugin.identity;
			if (!(plugins.indexOf(plugin) == plugins.size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}
}
