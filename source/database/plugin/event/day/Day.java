package database.plugin.event.day;

import java.time.LocalDate;
import database.plugin.event.Event;

public class Day extends Event {
	public Day(String name, LocalDate date) {
		super(name, date);
	}

	@Override public LocalDate updateYear() {
		LocalDate currentDate = LocalDate.now();
		if (currentDate.getMonthValue() > date.getMonthValue() || currentDate.getMonthValue() == date.getMonthValue() && currentDate.getDayOfMonth() > date.getDayOfMonth()) {
			return date.withYear(currentDate.getYear() + 1);
		}
		else {
			return date.withYear(currentDate.getYear());
		}
	}

	@Override protected String getAdditionToOutput(int year) {
		return "(" + date.getYear() + ")";
	}
}
