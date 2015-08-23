package database.plugin.refilling;

import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class RefillingPlugin extends InstancePlugin {
	public RefillingPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "refilling", new RefillingList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			create(request(new String[][] { { "refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?" }, { "value", "[0-9]{1,13}(\\.[0-9]*)?" }, { "distance", "[0-9]{1,13}(\\.[0-9]*)?" }, { "date", null } }));
			update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Command(tag = "show") public void show() throws InterruptedException, NotImplementedException {
		terminal.solutionOut(instanceList.output(null));
		graphicalUserInterface.waitForInput();
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "show":
				show();
				break;
			case "display":
				display();
				break;
		}
	}
}