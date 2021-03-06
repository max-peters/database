package database.plugin.calendar.element;

import java.time.LocalDate;

import database.plugin.calendar.CalendarElements;
import database.services.ServiceRegistry;
import database.services.settings.ISettingsProvider;

public class Day extends Event {
	public Day(String name, LocalDate date) {
		super(name, date);
	}

	@Override
	public String getAdditionToOutput() {
		return "(" + getDate().getYear() + ")";
	}

	@Override
	public int getPriority() {
		return ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters()
				.getCalendarElementPriority(CalendarElements.DAY);
	}

	@Override
	public LocalDate updateYear() {
		LocalDate currentDate = LocalDate.now();
		if (currentDate.getMonthValue() > getDate().getMonthValue()
				|| currentDate.getMonthValue() == getDate().getMonthValue()
						&& currentDate.getDayOfMonth() > getDate().getDayOfMonth()) {
			return getDate().withYear(currentDate.getYear() + 1);
		}
		else {
			return getDate().withYear(currentDate.getYear());
		}
	}
}
