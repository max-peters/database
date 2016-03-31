package database.plugin.task;

import java.util.HashMap;
import java.util.Map;
import database.plugin.Instance;

public class Task extends Instance {
	public String name;

	public Task(String name) {
		this.name = name;
	}

	@Override public boolean equals(Object object) {
		Task task;
		if (object != null && object.getClass().equals(this.getClass())) {
			task = (Task) object;
			if (name.equals(task.name)) {
				return true;
			}
		}
		return false;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("name", name);
		return parameter;
	}
}
