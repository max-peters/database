package database.plugin.event.allDayEvent;

import database.main.PluginContainer;
import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class AllDayEventPlugin extends EventPluginExtention {
	public AllDayEventPlugin(PluginContainer pluginContainer, Storage storage) {
		super(pluginContainer, "day", new AllDayEventList(), storage);
	}
}