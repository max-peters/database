package database.services.settings;

import java.util.Arrays;
import java.util.List;
import database.plugin.calendar.CalendarElements;

public class InternalParameters {
	protected List<CalendarElements>	calendarElementPriorityList;
	protected boolean					displayLogger;
	protected int						eventDisplayRange;
	protected int						revertStackSize;

	public InternalParameters() {
		displayLogger = true;
		eventDisplayRange = 7;
		revertStackSize = 1;
		calendarElementPriorityList = Arrays
			.asList(new CalendarElements[] { CalendarElements.HOLIDAY, CalendarElements.DAY, CalendarElements.BIRTHDAY, CalendarElements.APPOINTMENT });
	}

	public int getCalendarElementPriority(CalendarElements type) {
		return calendarElementPriorityList.indexOf(type);
	}

	public boolean getDisplayLogger() {
		return displayLogger;
	}

	public int getEventDisplayRange() {
		return eventDisplayRange;
	}

	public int getRevertStackSize() {
		return revertStackSize;
	}
}
