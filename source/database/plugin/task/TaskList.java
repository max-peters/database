package database.plugin.task;

import database.plugin.Instance;
import database.plugin.InstanceList;

public class TaskList extends InstanceList<Task> {
	@Override public String initialOutput() {
		String output = "";
		for (Instance instance : this) {
			output = output + "\u2610 " + ((Task) instance).name + System.getProperty("line.separator");
		}
		return output;
	}
}
