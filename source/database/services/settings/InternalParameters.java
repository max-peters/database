package database.services.settings;

import java.util.Arrays;
import java.util.List;

import database.plugin.calendar.CalendarElements;

public class InternalParameters {
	protected List<CalendarElements> calendarElementPriorityList;
	protected List<String> currencies;
	protected String currentCurrency;
	protected boolean displayLogger;
	protected int eventDisplayRange;
	protected int revertStackSize;

	public InternalParameters() {
		displayLogger = false;
		eventDisplayRange = 7;
		revertStackSize = 1;
		calendarElementPriorityList = Arrays.asList(new CalendarElements[] { CalendarElements.HOLIDAY,
				CalendarElements.DAY, CalendarElements.BIRTHDAY, CalendarElements.APPOINTMENT });
		currencies = Arrays.asList(new String[] { "EUR" });
		currentCurrency = "EUR";
	}

	public int getCalendarElementPriority(CalendarElements type) {
		return calendarElementPriorityList.indexOf(type);
	}

	public List<String> getCurrencies() {
		return currencies;
	}

	public String getCurrentCurrency() {
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

	public void setCurrentCurrency(String currentCurrency) {
		this.currentCurrency = currentCurrency;
	}
}
