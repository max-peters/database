package database.plugin.task;

import database.plugin.Instance;

public class Task extends Instance {
	public Task(String[][] parameter, TaskList list) {
		super(parameter, parameter[0][1], list);
	}

	String getTask() {
		return getParameter("name");
	}
}
