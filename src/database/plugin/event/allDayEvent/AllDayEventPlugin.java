package database.plugin.event.allDayEvent;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.event.EventExtention;

public class AllDayEventPlugin extends EventExtention {
	public AllDayEventPlugin(PluginContainer store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "day", new AllDayEventList());
		this.display = true;
	}
}