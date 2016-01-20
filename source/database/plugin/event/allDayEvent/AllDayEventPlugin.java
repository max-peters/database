package database.plugin.event.allDayEvent;

import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class AllDayEventPlugin extends EventPluginExtention {
	public AllDayEventPlugin(Storage storage) {
		super("day", new AllDayEventList(), storage);
	}
}