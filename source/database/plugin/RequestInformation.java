package database.plugin;

import java.util.HashMap;
import java.util.Map;

public class RequestInformation {
	private String				name;
	private Map<String, String>	map;

	public RequestInformation(String name) {
		this.name = name;
		this.map = new HashMap<String, String>();
	}

	public RequestInformation(String name, Map<String, String> parameter) {
		this.name = name;
		this.map = parameter;
	}

	public RequestInformation(String name, String key, String value) {
		this.name = name;
		this.map = new HashMap<String, String>();
		map.put(key, value);
	}

	public void put(String key, String value) {
		map.put(key, value);
	}

	public String getName() {
		return name;
	}

	public Map<String, String> getMap() {
		return map;
	}
}
