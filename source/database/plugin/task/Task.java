package database.plugin.task;

import org.w3c.dom.Element;
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

	@Override public void insertParameter(Element element) {
		element.setAttribute("name", name);
		element.setAttribute("category", category);
	}
}
