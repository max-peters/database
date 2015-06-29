package database.plugin.task;

import java.util.ArrayList;

import database.main.Instance;
import database.main.InstanceList;

public class TaskList extends InstanceList {
	public TaskList() {
	}

	public ArrayList<Task> getList() {
		ArrayList<Task> tasks = new ArrayList<Task>();
		for (Instance instance : list) {
			tasks.add((Task) instance);
		}
		return tasks;
	}

	public void add(String[] parameter) {
		list.add(new Task(parameter, this));
	}

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		return null;
	}

	public String initialOutput() {
		return null;
	}
}
