package database.main;

import java.util.ArrayList;

public abstract class InstanceList {
	protected ArrayList<Instance>	list;

	public InstanceList() {
		this.list = new ArrayList<Instance>();
	}

	public void remove(Instance toRemove) {
		list.remove(toRemove);
	}

	public ArrayList<String> getEntriesAsStrings() {
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : list) {
			strings.add(instance.identity);
		}
		return strings;
	}

	public abstract ArrayList<?> getList();

	public abstract String initialOutput();

	public abstract String output(String[] tags);

	public abstract void add(String[] parameter);
}
