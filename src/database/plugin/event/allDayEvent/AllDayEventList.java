package database.plugin.event.allDayEvent;

import database.plugin.event.EventList;

public class AllDayEventList extends EventList {
	public String output(String[] tags) {
		return null;
	}

	public void add(String[] parameter) {
		list.add(new AllDayEvent(parameter, this));
	}

	public void change(String[] parameter) {
	}
}
