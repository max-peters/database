package database.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.Storage;

public class PluginContainer {
	private ArrayList<Plugin>	plugins;
	private Storage				storage;

	public PluginContainer(Storage storage) {
		plugins = new ArrayList<>();
		this.storage = storage;
	}

	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
	}

	public void addToDocument(Document document, Element appendTo) {
		for (Plugin currentPlugin : plugins) {
			Element element = document.createElement(currentPlugin.identity);
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
			if (plugin.identity.equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	public String getPluginNameTagsAsRegesx(String[] array) {
		String regex = "(";
		ArrayList<String> list = new ArrayList<>();
		for (Plugin plugin : plugins) {
			boolean isAnnotationPresent = false;
			for (Method method : plugin.getClass().getMethods()) {
				if (method.isAnnotationPresent(Command.class)) {
					isAnnotationPresent = true;
				}
			}
			if (isAnnotationPresent) {
				list.add(plugin.identity);
			}
		}
		list.addAll(Arrays.asList(array));
		Collections.sort(list);
		for (String string : list) {
			regex += string + "|";
		}
		return regex.endsWith("|") ? regex.substring(0, regex.lastIndexOf("|")) + ")" : "()";
	}

	public Storage getStorage() {
		return storage;
	}

	public void initialOutput(ITerminal terminal) throws BadLocationException {
		for (Plugin plugin : plugins) {
			if (plugin.display) {
				plugin.initialOutput(terminal, this);
			}
		}
	}
}
