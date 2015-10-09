package database.plugin.event.birthday;

import java.util.Map;

import database.plugin.event.EventList;

public class BirthdayList extends EventList {
	@Override public void add(Map<String, String> parameter) {
		sortedAdd(new Birthday(parameter));
	}
}