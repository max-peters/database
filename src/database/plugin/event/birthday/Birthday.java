package database.plugin.event.birthday;

import database.main.date.Date;
import database.plugin.event.Event;
import database.plugin.event.EventList;

public class Birthday extends Event {
	public Birthday(String[][] parameter, BirthdayList list) {
		super(new String[][] { parameter[0], parameter[1] }, list);
	}

	@Override protected String output() {
		int nameLength = 0;
		int ageLength = 0;
		String newName = getName();
		for (Event event : ((EventList) list).getNearEvents()) {
			Birthday birthday = (Birthday) event;
			if (birthday.getName().length() > nameLength) {
				nameLength = birthday.getName().length();
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
		return Date.getDate().year.counter - getDate().year.counter;
	}
}
