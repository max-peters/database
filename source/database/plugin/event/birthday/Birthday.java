package database.plugin.event.birthday;

import database.main.date.Date;
import database.plugin.event.Event;

public class Birthday extends Event {
	public Birthday(String name, Date date) {
		super(name, date);
	}

	@Override protected String getAdditionToOutput() {
		return "[" + getAge() + "]";
	}

	private int getAge() {
		return Date.getCurrentDate().year.counter - date.year.counter;
	}
}
