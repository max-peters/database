package database.plugin.event.birthday;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.plugin.event.EventPluginExtention;

public class BirthdayPlugin extends EventPluginExtention {
	public BirthdayPlugin(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, graphicalUserInterface, administration, "birthday", new BirthdayList());
	}
}