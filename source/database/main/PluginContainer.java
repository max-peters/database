package database.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import database.plugin.Command;
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

	public void addToDocument(Document document, Element appendTo) {
		for (Plugin currentPlugin : plugins) {
			Element element = document.createElement(currentPlugin.getIdentity());
			currentPlugin.print(document, element);
			appendTo.appendChild(element);
		}
	}

	public void clear() {
		for (Plugin plugin : plugins) {
			if (plugin instanceof InstancePlugin<?>) {
				((InstancePlugin<?>) plugin).clearList();
			}
		}
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
			boolean isAnnotationPresent = false;
			for (Method method : plugin.getClass().getMethods()) {
				if (method.isAnnotationPresent(Command.class)) {
					isAnnotationPresent = true;
				}
			}
			if (isAnnotationPresent) {
				regex += plugin.getIdentity() + "|";
			}
		}
		return regex.endsWith("|") ? regex.substring(0, regex.lastIndexOf("|")) + ")" : "()";
	}

	public void initialOutput() throws BadLocationException {
		for (Plugin plugin : plugins) {
			if (plugin.getDisplay()) {
				plugin.initialOutput();
			}
		}
	}
}
