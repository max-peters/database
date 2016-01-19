package database.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public abstract class InstanceList {
	private ArrayList<Instance> list;

	public InstanceList() {
		list = new ArrayList<Instance>();
	}

	public abstract void add(Map<String, String> map) throws IOException;

	public boolean containsEquals(Map<String, String> parameter) {
		boolean contains = false;
		for (Instance instance : list) {
			contains = false;
			for (Entry<String, String> entry : parameter.entrySet()) {
				if (instance.getParameter().containsKey(entry.getKey()) && instance.getParameter(entry.getKey()).equals(entry.getValue())) {
					contains = true;
				}
				else {
					contains = false;
					break;
				}
			}
			if (contains && instance.getParameter().size() == parameter.size()) {
				return true;
			}
		}
		return false;
	}

	public boolean containsParts(Map<String, String> parameter) {
		boolean contains = false;
		for (Instance instance : list) {
			contains = false;
			for (Entry<String, String> entry : parameter.entrySet()) {
				if (instance.getParameter().containsKey(entry.getKey())) {
					if (instance.getParameter(entry.getKey()).equals(entry.getValue())) {
						contains = true;
					}
					else {
						contains = false;
						break;
					}
				}
			}
			if (contains) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Instance> getList() {
		return list;
	}

	public String initialOutput() {
		return null;
	}

	public void remove(Instance toRemove) {
		list.remove(toRemove);
	}
}
