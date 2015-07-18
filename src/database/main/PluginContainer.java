package database.main;

import java.util.ArrayList;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

public class PluginContainer {
	private ArrayList<Plugin>	plugins;

	public PluginContainer() {
		this.plugins = new ArrayList<Plugin>();
	}

	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
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

	@Getter public boolean getChanges() {
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

	@Setter public void setUnchanged() {
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin) {
				InstancePlugin current = (InstancePlugin) plugin;
				current.setChanges(false);
			}
		}
	}
}
