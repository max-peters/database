package database.plugin.event.birthday;

import database.plugin.event.EventList;

public class BirthdayList extends EventList {
	@Override public void add(String[][] parameter) {
		getList().add(new Birthday(parameter, this));
	}
}