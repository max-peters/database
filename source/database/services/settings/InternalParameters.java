package database.services.settings;

import java.util.Arrays;
import java.util.List;

import database.plugin.calendar.CalendarElements;
import database.plugin.expense.Currency;

public class InternalParameters {
	protected List<CalendarElements> calendarElementPriorityList;
	protected Currency currentCurrency;
	protected boolean displayLogger;
	protected int eventDisplayRange;
	protected int revertStackSize;

	public InternalParameters() {
		displayLogger = true;
		eventDisplayRange = 7;
		revertStackSize = 1;
		calendarElementPriorityList = Arrays.asList(new CalendarElements[] { CalendarElements.HOLIDAY,
				CalendarElements.DAY, CalendarElements.BIRTHDAY, CalendarElements.APPOINTMENT });
		currentCurrency = Currency.EUR;
	}

	public int getCalendarElementPriority(CalendarElements type) {
		return calendarElementPriorityList.indexOf(type);
	}

	public Currency getCurrentCurrency() {
		return currentCurrency;
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

	public void setCurrentCurrency(Currency currentCurrency) {
		this.currentCurrency = currentCurrency;
	}
}
