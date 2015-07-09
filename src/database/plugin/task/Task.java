package database.plugin.task;

import database.plugin.Instance;

public class Task extends Instance {
	public Task(String[][] parameter, TaskList list) {
		super(parameter, parameter[0][1], list);
	}

	@Override public String[][] getParameter() {
		return new String[][] { { "name", getParameter("name") } };
	}
}
