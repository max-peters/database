package database.plugin.event.birthday;

import java.util.Map;
import database.main.date.Date;
import database.plugin.event.Event;

public class Birthday extends Event {
	public Birthday(Map<String, String> parameter) {
		super(parameter);
	}

	@Override protected String appendToOutput() {
		return "[" + getAge() + "]";
	}

	private int getAge() {
		return Date.getCurrentDate().year.counter - date.year.counter;
	}
}
