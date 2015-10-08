package database.plugin.task;

import java.util.Map;

import database.plugin.Instance;

public class Task extends Instance {
	public Task(Map<String, String> parameter, TaskList list) {
		super(parameter, list);
	}

	protected String getTask() {
		return getParameter("name");
	}

	@Override public String getIdentity() {
		return getTask();
	}
}
