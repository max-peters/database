package database.plugin.calendar.element;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import database.plugin.Instance;

public abstract class CalendarElement extends Instance implements Comparable<CalendarElement> {
	@Override public int compareTo(CalendarElement element) {
		int x = orderDate().compareTo(element.orderDate());
		if (x < 0) {
			return -1;
		}
		else if (x == 0) {
			return getPriority() - element.getPriority();
		}
		else {
			return 1;
		}
	}

	public abstract String getAdditionToOutput();

	public abstract LocalDate getDate();

	public abstract String getName();

	public abstract String getSpezification();

	public abstract int getPriority();

	public boolean isNear(int days) {
		if (ChronoUnit.DAYS.between(LocalDate.now(), updateYear()) <= days) {
			return true;
		}
		else {
			return false;
		}
	}

	public abstract boolean isPast();

	public abstract LocalDateTime orderDate();

	public abstract LocalDate updateYear();
}
