package database.plugin.event.holiday;

import java.io.IOException;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.InstancePlugin;

public class HolidayPlugin extends InstancePlugin {
	public HolidayPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "holiday", new HolidayList());
	}
}
