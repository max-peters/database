package database.plugin.calendar.element;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Event extends CalendarElement {
	private LocalDate	date;
	private String		name;

	public Event(String name, LocalDate date) {
		this.name = name;
		this.date = date;
	}

	@Override public abstract String getAdditionToOutput();

	@Override public LocalDate getDate() {
		return date;
	}

	@Override public String getName() {
		return name;
	}

	@Override public boolean isPast() {
		if (LocalDate.now().isAfter(updateYear())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override public LocalDateTime orderDate() {
		return updateYear().atTime(LocalTime.MIN);
	}

	public String getSpezification() {
		return "";
	}
}
