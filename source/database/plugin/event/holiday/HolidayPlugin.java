package database.plugin.event.holiday;

import java.io.IOException;
import database.main.PluginContainer;
import database.plugin.InstancePlugin;

public class HolidayPlugin extends InstancePlugin {
	public HolidayPlugin(PluginContainer pluginContainer) throws IOException {
		super(pluginContainer, "holiday", new HolidayList());
	}
}
