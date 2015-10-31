package database.plugin.event.allDayEvent;

import database.main.PluginContainer;
import database.plugin.event.EventPluginExtention;

public class AllDayEventPlugin extends EventPluginExtention {
	public AllDayEventPlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "day", new AllDayEventList());
	}
}