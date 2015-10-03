package database.plugin;

import java.util.ArrayList;
import java.util.Map;

public abstract class InstanceList {
	private ArrayList<Instance>	list;

	public InstanceList() {
		this.list = new ArrayList<Instance>();
	}

	public void remove(Instance toRemove) {
		list.remove(toRemove);
	}

	public ArrayList<Instance> getList() {
		return list;
	}

	public String initialOutput() {
		return null;
	}

	public String output(Map<String, String> parameter) {
		return null;
	}

	public abstract void add(Map<String, String> map);
}
