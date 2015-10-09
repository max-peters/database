package database.plugin.event.birthday;

import java.util.Map;

import database.main.date.Date;
import database.plugin.event.Event;

public class Birthday extends Event {
	public Birthday(Map<String, String> parameter) {
		super(parameter);
	}

	@Override protected String output() {
		// int nameLength = 0;
		// int ageLength = 0;
		// String newName = getName();
		// for (Event event : ((EventList) getInstanceList()).getNearEvents()) {
		// Birthday birthday = (Birthday) event;
		// if (birthday.getName().length() > nameLength) {
		// nameLength = birthday.getName().length();
		// }
		// if (String.valueOf(birthday.getAge()).length() > ageLength) {
		// ageLength = String.valueOf(birthday.getAge()).length();
		// }
		// }
		// for (newName.length(); newName.length() < nameLength + 1;) {
		// newName = newName + " ";
		// }
		// return updateYear().toString() + " - " + newName + " [" + String.format("%" + ageLength + "s", getAge()).replace(' ', '0') + "]";
		return updateYear().toString() + " - " + getName() + " [" + getAge() + "]";
	}

	private int getAge() {
		return Date.getCurrentDate().year.counter - getDate().year.counter;
	}
}
