package database.plugin.event.birthday;

import database.main.date.Date;
import database.plugin.event.Event;
import database.plugin.event.EventList;

public class Birthday extends Event {
	public Birthday(String[][] parameter, BirthdayList list) {
		super(new String[][] { parameter[0], parameter[1] }, list);
	}

	@Override public String[][] getParameter() {
		return new String[][] { { "name", getParameter("name") }, { "date", getParameter("date") } };
	}

	@Override protected String output() {
		int nameLength = 0;
		int ageLength = 0;
		String newName = getParameter("name");
		for (Event event : ((EventList) list).getNearEvents()) {
			Birthday birthday = (Birthday) event;
			if (birthday.getParameter("name").length() > nameLength) {
				nameLength = birthday.getParameter("name").length();
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
		return Date.getDate().year.counter - new Date(getParameter("date")).year.counter;
	}
}
