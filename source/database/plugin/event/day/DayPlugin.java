package database.plugin.event.day;

import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class DayPlugin extends EventPluginExtention {
	public DayPlugin(Storage storage) {
		super("day", new DayList(), storage);
	}
}