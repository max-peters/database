package database.main;

import java.util.ArrayList;

import database.plugin.InstancePlugin;
import database.plugin.Plugin;

public class Store {
	private ArrayList<InstancePlugin>	instancePlugins;
	private ArrayList<Plugin>			plugins;
	private ArrayList<String>			storage;
	private boolean						changes;

	public Store() {
		this.instancePlugins = new ArrayList<InstancePlugin>();
		this.plugins = new ArrayList<Plugin>();
		this.storage = new ArrayList<String>();
		this.changes = false;
	}

	public void addPlugin(Plugin plugin) {
		if (plugin instanceof InstancePlugin) {
			instancePlugins.add((InstancePlugin) plugin);
		}
		plugins.add(plugin);
	}

	public ArrayList<InstancePlugin> getPlugins() {
		return instancePlugins;
	}

	public Plugin getPlugin(String identity) {
		for (Plugin plugin : plugins) {
			if (plugin.getIdentity().equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	public InstancePlugin getInstancePlugin(String identity) {
		for (InstancePlugin plugin : instancePlugins) {
			if (plugin.getIdentity().equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	public ArrayList<String> getListToWrite() {
		ArrayList<String> listToWrite = new ArrayList<String>();
		for (InstancePlugin plugin : instancePlugins) {
			listToWrite.addAll(plugin.getStringList());
		}
		for (InstancePlugin plugin : instancePlugins) {
			listToWrite.add(plugin.getDisplayAsString());
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

	public String getInstancePluginNameTagsAsRegesx() {
		String regex = "(";
		for (InstancePlugin plugin : instancePlugins) {
			regex = regex + plugin.getIdentity();
			if (!(instancePlugins.indexOf(plugin) == instancePlugins.size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
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
