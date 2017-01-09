package database.plugin.event.holiday;

import java.time.LocalDate;
import database.plugin.event.Event;

public class Holiday extends Event {
	public Holiday(String name, LocalDate date) {
		super(name, date);
	}

	@Override public LocalDate updateYear() {
		return date;
	}

	@Override protected String getAdditionToOutput() {
		return "";
	}
}
