package database.plugin.task;

import java.util.Map;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class TaskList extends InstanceList {
	@Override public String initialOutput() {
		String output = "";
		for (Instance instance : getIterable()) {
			output = output + "\u2610 " + ((Task) instance).name + System.getProperty("line.separator");
		}
		return output;
	}

	@Override public void add(Map<String, String> map) {
		list.add(new Task(map));
	}
}
