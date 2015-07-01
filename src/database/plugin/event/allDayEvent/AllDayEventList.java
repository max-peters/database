package database.plugin.event.allDayEvent;

import database.plugin.event.EventList;

public class AllDayEventList extends EventList {
	public void add(String[] parameter) {
		list.add(new AllDayEvent(parameter, this));
	}
}
