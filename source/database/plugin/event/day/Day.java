package database.plugin.event.day;

import database.main.date.Date;
import database.plugin.event.Event;

public class Day extends Event {
	public Day(String name, Date date) {
		super(name, date);
	}

	@Override protected String appendToOutput() {
		return "(" + date.year + ")";
	}
}