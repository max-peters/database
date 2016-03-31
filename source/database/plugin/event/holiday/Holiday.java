package database.plugin.event.holiday;

import database.main.date.Date;
import database.plugin.event.Event;

public class Holiday extends Event {
	public Holiday(String name, Date date) {
		super(name, date);
	}

	@Override public Date updateYear() {
		return date;
	}

	@Override protected String appendToOutput() {
		return "";
	}
}
