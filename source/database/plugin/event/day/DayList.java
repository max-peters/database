package database.plugin.event.day;

import java.util.Map;
import database.plugin.event.EventList;

public class DayList extends EventList {
	@Override public void add(Map<String, String> parameter) {
		sortedAdd(new Day(parameter));
	}
}
