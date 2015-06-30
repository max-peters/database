package database.plugin.task;

import database.main.InstanceList;

public class TaskList extends InstanceList {
	public TaskList() {
	}

	public void add(String[] parameter) {
		list.add(new Task(parameter, this));
	}

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		return null;
	}

	public String initialOutput() {
		return null;
	}
}
