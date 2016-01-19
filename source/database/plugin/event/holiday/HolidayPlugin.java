package database.plugin.event.holiday;

import database.main.PluginContainer;
import database.plugin.InstancePlugin;

public class HolidayPlugin extends InstancePlugin {
	public HolidayPlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "holiday", new HolidayList());
	}
}
