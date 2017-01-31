package database.services.settings;

import java.util.LinkedList;
import java.util.List;
import database.plugin.calendar.CalendarElements;

public class InternalParameters {
	public List<CalendarElements>	calendarElementPriorityList	= new LinkedList<>();
	protected boolean				displayLogger				= true;
	protected int					eventDisplayRange			= 7;
	protected int					revertStackSize				= 1;

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
