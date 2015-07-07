package database.plugin.event.allDayEvent;

import database.plugin.event.EventList;

public class AllDayEventList extends EventList {
	@Override public void add(String[] parameter) {
		getList().add(new AllDayEvent(parameter, this));
	}
}
