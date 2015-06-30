package database.plugin.event.allDayEvent.birthday;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.event.allDayEvent.AllDayEventPlugin;

public class BirthdayPlugin extends AllDayEventPlugin {
	public BirthdayPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration);
		this.instanceList = new BirthdayList();
		this.identity = "birthday";
	}
}
