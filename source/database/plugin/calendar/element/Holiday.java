package database.plugin.calendar.element;

import java.time.LocalDate;
import database.plugin.calendar.CalendarElements;
import database.services.ServiceRegistry;
import database.services.settings.ISettingsProvider;

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

	@Override public int getPriority() {
		return ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters().getCalendarElementPriority(CalendarElements.HOLIDAY);
	}
}
