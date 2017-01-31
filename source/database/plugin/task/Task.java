package database.plugin.task;

import database.plugin.Instance;

public class Task extends Instance {
	private String name;

	public Task(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
