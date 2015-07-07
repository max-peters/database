package database.plugin.event;

import java.util.concurrent.CancellationException;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstanceList;
import database.plugin.Plugin;

public abstract class EventExtention extends Plugin {
	public EventExtention(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		super(store, terminal, graphicalUserInterface, administration, identity, instanceList);
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			instanceList.add(request(new String[][] { { "name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)" }, { "date", null } }));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Override public void conduct(String command) throws InterruptedException {
	}

	@Getter public InstanceList getInstanceList() {
		return instanceList;
	}
}
