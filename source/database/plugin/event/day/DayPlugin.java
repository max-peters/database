package database.plugin.event.day;

import java.util.Map;
import database.main.date.Date;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage) {
		super("day", storage);
	}

	@Override public Day create(Map<String, String> parameter) {
		return new Day(parameter.get("name"), new Date(parameter.get("date")));
	}
}