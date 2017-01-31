package database.plugin.calendar.element;

import java.time.LocalDate;
import database.plugin.calendar.CalendarElements;
import database.services.ServiceRegistry;
import database.services.settings.ISettingsProvider;

public class Birthday extends Event {
	public Birthday(String name, LocalDate date) {
		super(name, date);
	}

	@Override public String getAdditionToOutput() {
		return "[" + (updateYear().getYear() - getDate().getYear()) + "]";
	}

	@Override public LocalDate updateYear() {
		LocalDate currentDate = LocalDate.now();
		if (currentDate.getMonthValue() > getDate().getMonthValue()
			|| currentDate.getMonthValue() == getDate().getMonthValue() && currentDate.getDayOfMonth() > getDate().getDayOfMonth()) {
			return getDate().withYear(currentDate.getYear() + 1);
		}
		else {
			return getDate().withYear(currentDate.getYear());
		}
	}

	@Override public int getPriority() {
		return ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters().getCalendarElementPriority(CalendarElements.BIRTHDAY);
	}
}
