package database.plugin.task;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.plugin.Instance;

public class Task extends Instance {
	public Task(String[][] parameter, TaskList list) {
		super(parameter, parameter[0][1], list);
	}

	@Getter String getTask() {
		return getParameter("name");
	}
}
