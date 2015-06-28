package database.plugin.task;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;

public class TaskPlugin extends Plugin {
	public TaskPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "task");
		this.instanceList = new TaskList();
	}

	protected String[][] getCreateInformation() {
		String[][] createInformation = { { "task", ".*" } };
		return createInformation;
	}

	protected String[][] getShowInformation() {
		return null;
	}

	protected String[][] getChangeInformation() {
		return null;
	}

	protected String show(String[] parameter) {
		return null;
	}

	protected void change(String[] parameter) {
	}
}
