package database.plugin.event.day;

import java.util.Map;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage) {
		super("day", storage);
	}

	@Override public Day create(Map<String, String> map) {
		return new Day(map);
	}
}