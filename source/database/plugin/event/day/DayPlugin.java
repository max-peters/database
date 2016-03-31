package database.plugin.event.day;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage) {
		super("day", new DayList(), storage);
	}

	@Override public Day create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
															NoSuchMethodException, SecurityException {
		return new Day(map);
	}
}