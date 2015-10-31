package database.plugin.event.birthday;

import database.main.PluginContainer;
import database.plugin.event.EventPluginExtention;

public class BirthdayPlugin extends EventPluginExtention {
	public BirthdayPlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "birthday", new BirthdayList());
	}
}