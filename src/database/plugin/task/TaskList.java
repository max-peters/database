package database.plugin.task;

import database.plugin.InstanceList;

public class TaskList extends InstanceList {
	@Override public void add(String[] parameter) {
		getList().add(new Task(parameter, this));
	}
}
