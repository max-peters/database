package database.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

public abstract class InstanceList {
	protected ArrayList<Instance> list;

	public InstanceList() {
		list = new ArrayList<Instance>();
	}

	public abstract void add(Map<String, String> map)	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException, IOException;

	public void clear() {
		list.clear();
	}

	public boolean contains(Map<String, String> parameter)	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
															NoSuchMethodException, SecurityException {
		boolean contains = false;
		for (Instance instance : list) {
			contains = false;
			if (instance.equals(parameter)) {
				contains = true;
			}
			else {
				contains = false;
				break;
			}
			if (contains) {
				return true;
			}
		}
		return false;
	}

	public Instance get(int index) {
		if (index < 0 || index > list.size() - 1) {
			return null;
		}
		return list.get(index);
	}

	public Iterable<Instance> getIterable() {
		Iterable<Instance> iterable = list;
		return iterable;
	}

	public String initialOutput() {
		return null;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void remove(Instance toRemove) {
		list.remove(toRemove);
	}
}
