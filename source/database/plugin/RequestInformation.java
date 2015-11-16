package database.plugin;

import java.util.HashMap;
import java.util.Map;

public class RequestInformation {
	private Map<String, String>	map;
	private String				name;

	public RequestInformation(String name) {
		this.name = name;
		map = new HashMap<String, String>();
	}

	public RequestInformation(String name, Map<String, String> parameter) {
		this.name = name;
		map = parameter;
	}

	public RequestInformation(String name, String key, String value) {
		this.name = name;
		map = new HashMap<String, String>();
		map.put(key, value);
	}

	public Map<String, String> getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public void put(String key, String value) {
		map.put(key, value);
	}
}
