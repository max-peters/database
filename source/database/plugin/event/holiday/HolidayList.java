package database.plugin.event.holiday;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import database.plugin.event.EventList;

public class HolidayList extends EventList {
	@Override public void add(Map<String, String> map)	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException, IOException {
		sortedAdd(new Holiday(map));
	}
}
