package database.plugin.event.allDayEvent.birthday;

import database.main.date.Date;
import database.plugin.event.Event;
import database.plugin.event.EventList;
import database.plugin.event.allDayEvent.AllDayEvent;

public class Birthday extends AllDayEvent {
	public Birthday(String[] parameter, BirthdayList list) {
		super(new String[] { parameter[0], parameter[1], "true" }, list);
	}

	@Override public String toString() {
		return "event : birthday / " + name + " / " + date.toString();
	}

	@Override protected String output() {
		int nameLength = 0;
		int ageLength = 0;
		String newName = name;
		for (Event event : ((EventList) list).getNearEvents()) {
			Birthday birthday = (Birthday) event;
			if (birthday.name.length() > nameLength) {
				nameLength = birthday.name.length();
			}
			if (String.valueOf(birthday.getAge()).length() > ageLength) {
				ageLength = String.valueOf(birthday.getAge()).length();
			}
		}
		for (newName.length(); newName.length() < nameLength + 1;) {
			newName = newName + " ";
		}
		return updateYear().toString() + " - " + newName + " [" + String.format("%" + ageLength + "s", getAge()).replace(' ', '0') + "]";
	}

	private int getAge() {
		return Date.getDate().year.counter - date.year.counter;
	}
}
