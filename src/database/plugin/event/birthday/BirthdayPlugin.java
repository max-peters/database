package database.plugin.event.birthday;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.event.EventExtention;
import database.plugin.event.birthday.BirthdayList;

public class BirthdayPlugin extends EventExtention {
	public BirthdayPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "birthday", new BirthdayList());
		this.display = true;
	}
}