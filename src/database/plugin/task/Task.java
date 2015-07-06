package database.plugin.task;

import database.plugin.Instance;

public class Task extends Instance {
	String	task;

	public Task(String[] parameter, TaskList list) {
		super(parameter[0], list);
		this.task = parameter[0];
	}

	public String toString() {
		return "task" + " : " + task;
	}
}
