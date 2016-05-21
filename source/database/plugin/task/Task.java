package database.plugin.task;

import database.plugin.Instance;

public class Task extends Instance {
	public String	category;
	public String	name;

	public Task(String name, String category) {
		this.name = name;
		this.category = category;
	}
}
