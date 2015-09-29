package database.plugin.event.allDayEvent;

import java.util.Map;

import database.plugin.event.EventList;

public class AllDayEventList extends EventList {
	@Override public void add(Map<String, String> parameter) {
		getList().add(new AllDayEvent(parameter, this));
	}
}
