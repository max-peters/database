package database.plugin.task;

import database.plugin.Instance;
import database.plugin.InstanceList;

public class TaskList extends InstanceList {
	@Override public void add(String[][] parameter) {
		getList().add(new Task(parameter, this));
	}

	@Override public String initialOutput() {
		String output = "";
		for (Instance instance : getList()) {
			output = output + "\u2610 " + instance.identity + "\r\n";
		}
		return output;
	}
}
