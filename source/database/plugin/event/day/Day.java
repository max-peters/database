package database.plugin.event.day;

import java.time.LocalDate;
import database.plugin.event.Event;

public class Day extends Event {
	public Day(String name, LocalDate date) {
		super(name, date);
	}

	@Override protected String getAdditionToOutput() {
		return "(" + date.getYear() + ")";
	}
}