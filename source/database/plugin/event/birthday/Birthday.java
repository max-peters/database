package database.plugin.event.birthday;

import java.time.LocalDate;
import database.plugin.event.Event;

public class Birthday extends Event {
	public Birthday(String name, LocalDate date) {
		super(name, date);
	}

	@Override public LocalDate updateYear() {
		LocalDate currentDate = LocalDate.now();
		if (currentDate.getMonthValue() > date.getMonthValue()
			|| currentDate.getMonthValue() == date.getMonthValue() && currentDate.getDayOfMonth() > date.getDayOfMonth()) {
			return date.withYear(currentDate.getYear() + 1);
		}
		else {
			return date.withYear(currentDate.getYear());
		}
	}

	@Override protected String getAdditionToOutput() {
		return "[" + (updateYear().getYear() - this.date.getYear()) + "]";
	}
}
