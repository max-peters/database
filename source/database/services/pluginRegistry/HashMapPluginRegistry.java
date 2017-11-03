package database.services.pluginRegistry;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.text.BadLocationException;

import database.main.userInterface.ITerminal;
import database.main.userInterface.StringType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.services.ServiceRegistry;

public class HashMapPluginRegistry implements IPluginRegistry {
	private Map<Class<? extends Plugin>, Plugin> map;

	public HashMapPluginRegistry() {
		map = new LinkedHashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getPlugin(Class<T> type) {
		return (T) map.get(type);
	}

	@Override
	public Plugin getPlugin(String identity) {
		for (Plugin plugin : map.values()) {
			if (plugin.identity.equals(identity)) {
				return plugin;
			}
		}
		return null;
	}

	@Override
	public String getPluginNameTagsAsRegex(String... args) {
		String regex = "(";
		ArrayList<String> list = new ArrayList<>();
		for (Plugin plugin : map.values()) {
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
		list.addAll(Arrays.asList(args));
		Collections.sort(list);
		for (String string : list) {
			regex += string + "|";
		}
		return regex.endsWith("|") ? regex.substring(0, regex.lastIndexOf("|")) + ")" : "()";
	}

	@Override
	public void initialOutput() throws BadLocationException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		for (Plugin plugin : map.values()) {
			if (plugin.display) {
				plugin.initialOutput();
			}
		}
		terminal.getLineOfCharacters('-', StringType.MAIN);
		terminal.newLine(StringType.MAIN);
	}

	@Override
	public void register(Plugin plugin) {
		map.put(plugin.getClass(), plugin);
	}
}
