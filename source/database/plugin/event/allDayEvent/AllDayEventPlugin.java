package database.plugin.event.allDayEvent;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.event.EventPluginExtention;

public class AllDayEventPlugin extends EventPluginExtention {
	public AllDayEventPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "day", new AllDayEventList());
	}
}