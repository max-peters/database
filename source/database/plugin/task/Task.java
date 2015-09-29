package database.plugin.task;

import java.util.Map;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.plugin.Instance;

public class Task extends Instance {
	public Task(Map<String, String> parameter, TaskList list) {
		super(parameter, "task", list);
	}

	@Getter String getTask() {
		return getParameter("name");
	}
}
