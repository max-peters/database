package database.plugin.event.day;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import database.plugin.event.EventList;

public class DayList extends EventList {
	@Override public void add(Map<String, String> map)	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException, IOException {
		sortedAdd(new Day(map));
	}
}
