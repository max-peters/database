package database.plugin.task;

import java.util.HashMap;
import java.util.Map;
import database.plugin.Instance;
import database.plugin.subject.Subject;

public class Task extends Instance {
	public String name;

	public Task(Map<String, String> parameter) {
		name = parameter.get("name");
	}

	@Override public boolean equals(Object object) {
		Subject subject;
		if (object != null && object.getClass().equals(this.getClass())) {
			subject = (Subject) object;
			if (name.equals(subject.name)) {
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
