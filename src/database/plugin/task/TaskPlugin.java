package database.plugin.task;

import java.util.concurrent.CancellationException;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class TaskPlugin extends InstancePlugin {
	public TaskPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "task", new TaskList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			create(request(new String[][] { { "name", ".+" } }));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "store":
				store();
				break;
			case "display":
				display();
				break;
		}
	}
}