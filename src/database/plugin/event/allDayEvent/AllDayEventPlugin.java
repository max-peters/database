package database.plugin.event.allDayEvent;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.event.EventExtention;

public class AllDayEventPlugin extends EventExtention {
	public AllDayEventPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "all day event", new AllDayEventList());
		this.display = true;
	}
}