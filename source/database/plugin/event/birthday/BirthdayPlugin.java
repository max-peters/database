package database.plugin.event.birthday;

import database.main.PluginContainer;
import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class BirthdayPlugin extends EventPluginExtention {
	public BirthdayPlugin(PluginContainer pluginContainer, Storage storage) {
		super(pluginContainer, "birthday", new BirthdayList(), storage);
	}
}