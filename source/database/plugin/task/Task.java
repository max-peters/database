package database.plugin.task;

import java.util.Map;
import database.plugin.Instance;

public class Task extends Instance {
	public Task(Map<String, String> parameter) {
		super(parameter);
	}

	protected String getTask() {
		return getParameter("name");
	}
}
