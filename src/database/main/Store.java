package database.main;

import java.util.ArrayList;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
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

	public void addToStorage(String toAdd) {
		storage.add(toAdd);
	}

	@Setter public void setChanges(boolean changes) {
		this.changes = changes;
	}

	@Getter public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	@Getter public Plugin getPlugin(String identity) {
		for (Plugin plugin : plugins) {
			if (plugin.getIdentity().equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	@Getter public ArrayList<String> getStorage() {
		return storage;
	}

	@Getter public boolean getChanges() {
		return changes;
	}

	@Getter public String getPluginNameTagsAsRegesx() {
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
