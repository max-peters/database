package database.plugin.event.holiday;

import java.util.Map;
import database.plugin.event.EventList;

public class HolidayList extends EventList {
	@Override public void add(Map<String, String> parameter) {
		sortedAdd(new Holiday(parameter));
	}
}
