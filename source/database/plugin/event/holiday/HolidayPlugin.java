package database.plugin.event.holiday;

import database.main.PluginContainer;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class HolidayPlugin extends InstancePlugin {
	public HolidayPlugin(PluginContainer pluginContainer, Storage storage) {
		super(pluginContainer, "holiday", new HolidayList(), storage);
	}
}
