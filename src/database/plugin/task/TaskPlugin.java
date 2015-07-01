package database.plugin.task;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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

	public String[][] getCreateInformation() {
		String[][] createInformation = { { "task", ".+" } };
		return createInformation;
	}

	public String[][] getShowInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}

	public String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
