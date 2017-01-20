package database.plugin.element;

import java.time.LocalDate;

public class Holiday extends Event {
	public Holiday(String name, LocalDate date) {
		super(name, date);
	}

	@Override public String getAdditionToOutput() {
		return "";
	}

	@Override public LocalDate updateYear() {
		return getDate();
	}
}
