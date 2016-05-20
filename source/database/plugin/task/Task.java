package database.plugin.task;

import database.plugin.Instance;

public class Task extends Instance {
	public String	category;
	public String	name;

	public Task(String name, String category) {
		this.name = name;
		this.category = category;
	}

	@Override public boolean equals(Object object) {
		Task task;
		if (object != null && object.getClass().equals(this.getClass())) {
			task = (Task) object;
			if (name.equals(task.name) && category.equals(task.category)) {
				return true;
			}
		}
		return false;
	}
}
