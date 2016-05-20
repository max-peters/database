package database.plugin.event.birthday;

import java.time.LocalDate;
import database.plugin.event.Event;

public class Birthday extends Event {
	public Birthday(String name, LocalDate date) {
		super(name, date);
	}

	@Override protected String getAdditionToOutput() {
		return "[" + getAge() + "]";
	}

	private int getAge() {
		return LocalDate.now().getYear() - date.getYear();
	}
}
