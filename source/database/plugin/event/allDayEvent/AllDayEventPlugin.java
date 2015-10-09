package database.plugin.event.allDayEvent;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.plugin.event.EventPluginExtention;

public class AllDayEventPlugin extends EventPluginExtention {
	public AllDayEventPlugin(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, graphicalUserInterface, administration, "day", new AllDayEventList());
	}
}