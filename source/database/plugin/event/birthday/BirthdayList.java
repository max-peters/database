package database.plugin.event.birthday;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import database.plugin.event.EventList;

public class BirthdayList extends EventList {
	@Override public void add(Map<String, String> map)	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException, IOException {
		sortedAdd(new Birthday(map));
	}
}
