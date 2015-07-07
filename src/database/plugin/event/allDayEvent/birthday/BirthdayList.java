package database.plugin.event.allDayEvent.birthday;

import database.plugin.event.allDayEvent.AllDayEventList;

public class BirthdayList extends AllDayEventList {
	@Override public void add(String[] parameter) {
		getList().add(new Birthday(parameter, this));
	}
}