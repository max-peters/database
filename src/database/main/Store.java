package database.main;

import java.util.ArrayList;

import database.plugin.InstancePlugin;
import database.plugin.Plugin;

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
		plugins.add(plugin);
	}

	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	public Plugin getPlugin(String identity) {
		for (Plugin plugin : plugins) {
			if (plugin.getIdentity().equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	public ArrayList<String> getListToWrite() {
		ArrayList<String> listToWrite = new ArrayList<String>();
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin) {
				listToWrite.addAll(((InstancePlugin) plugin).getStringList());
			}
		}
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin) {
				listToWrite.add(((InstancePlugin) plugin).getDisplayAsString());
			}
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

	public String getPluginNameTagsAsRegesx() {
		String regex = "(";
		for (Plugin plugin : plugins) {
			regex = regex + plugin.getIdentity();
			if (!(plugins.indexOf(plugin) == plugins.size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}
}
