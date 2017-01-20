package database.main.autocompletition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HashMapAutocomplete implements IAutocomplete {
	private Map<Tupel<String, String>, Integer> map;

	public HashMapAutocomplete() {
		map = new HashMap<>();
	}

	public HashMapAutocomplete(String regex) {
		map = new HashMap<>();
		for (String s : regex.replace("(", "").replace(")", "").split("\\|")) {
			add(s);
		}
		add("help");
		add("back");
	}

	public void add(String first) {
		Tupel<String, String> t = new Tupel<>(first, "");
		map.put(t, map.getOrDefault(t, 0) + 1);
	}

	public void add(String first, String second) {
		Tupel<String, String> t = new Tupel<>(first, second);
		map.put(t, map.getOrDefault(t, 0) + 1);
	}

	@Override public String getCorrespondingString(String s) {
		for (Tupel<String, String> tupel : map.keySet()) {
			if (tupel.first.equalsIgnoreCase(s)) {
				return tupel.first;
			}
		}
		return s;
	}

	@Override public String getMostUsedString(String prefix, String second) {
		Tupel<String, String> t = new Tupel<>("", "");
		for (Entry<Tupel<String, String>, Integer> entry : map.entrySet()) {
			if (second.isEmpty() || containsPair(entry.getKey().first, second)) {
				if (entry.getKey().first.toLowerCase().startsWith(prefix.toLowerCase()) && entry.getValue() > map.getOrDefault(t, 0)) {
					t = entry.getKey();
				}
			}
		}
		return second.isEmpty() && prefix.isEmpty() ? "" : t.first.isEmpty() ? "" : t.first.substring(prefix.length());
	}

	private boolean containsPair(String first, String second) {
		for (Tupel<String, String> entry : map.keySet()) {
			if (entry.first.equals(first) && entry.second.equals(second)) {
				return true;
			}
		}
		return false;
	}
}
