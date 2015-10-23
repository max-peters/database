package database.plugin.event.holiday;

import java.io.IOException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.plugin.InstancePlugin;

public class HolidayPlugin extends InstancePlugin {
	public HolidayPlugin(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super(pluginContainer, graphicalUserInterface, administration, "holiday", new HolidayList());
	}
}
