package database.plugin.task;

import java.util.Map;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class TaskList extends InstanceList {
	@Override public void add(Map<String, String> parameter) {
		getList().add(new Task(parameter));
	}

	@Override public String initialOutput() {
		String output = "";
		for (Instance instance : getList()) {
			output = output + "\u2610 " + ((Task) instance).getTask() + System.getProperty("line.separator");
		}
		return output;
	}
}
