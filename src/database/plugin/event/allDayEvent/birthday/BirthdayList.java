package database.plugin.event.allDayEvent.birthday;

import database.plugin.event.allDayEvent.AllDayEventList;

public class BirthdayList extends AllDayEventList {
	public void add(String[] parameter) {
		list.add(new Birthday(parameter, this));
	}
}