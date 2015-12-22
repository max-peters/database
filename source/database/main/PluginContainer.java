package database.main;

import java.util.ArrayList;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

public class PluginContainer {
	private ArrayList<Plugin> plugins;

	public PluginContainer() {
		plugins = new ArrayList<Plugin>();
	}

	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
	}

	public void clearLists() {
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin) {
				InstancePlugin current = (InstancePlugin) plugin;
				current.clearList();
			}
		}
	}

	public boolean getChanges() {
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin) {
				InstancePlugin current = (InstancePlugin) plugin;
				if (current.getChanges()) {
					return true;
				}
			}
		}
		return false;
	}

	public Plugin getPlugin(String identity) {
		for (Plugin plugin : plugins) {
			if (plugin.getIdentity().equals(identity)) {
				return plugin;
			}
		}
		return null;
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

	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}

	public void setUnchanged() {
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin) {
				InstancePlugin current = (InstancePlugin) plugin;
				current.setChanges(false);
			}
		}
	}
}
