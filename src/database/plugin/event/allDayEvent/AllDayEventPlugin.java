package database.plugin.event.allDayEvent;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.event.EventPlugin;

public class AllDayEventPlugin extends EventPlugin {
	public AllDayEventPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration);
		this.instanceList = new AllDayEventList();
		this.identity = "allDayEvent";
	}

	protected String[][] getCreateInformation() {
		String[][] createInformation = { { "name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)" }, { "date", null } };
		return createInformation;
	}
}
