package database.services.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.plugin.calendar.CalendarElements;
import database.plugin.expense.Currency;

public class InternalParameters {
	protected List<CalendarElements> calendarElementPriorityList;
	protected Currency currentCurrency;
	protected Map<String, Double> defaultExchangeRates;
	protected boolean displayLogger;
	protected int eventDisplayRange;
	protected int revertStackSize;

	public InternalParameters() {
		displayLogger = true;
		eventDisplayRange = 7;
		revertStackSize = 1;
		calendarElementPriorityList = Arrays.asList(new CalendarElements[] { CalendarElements.HOLIDAY,
				CalendarElements.DAY, CalendarElements.BIRTHDAY, CalendarElements.APPOINTMENT });
		defaultExchangeRates = new HashMap<String, Double>();
		for (Currency currency : Currency.values()) {
			defaultExchangeRates.put(currency.toString(), 1.0);
		}
		currentCurrency = Currency.EUR;
	}

	public int getCalendarElementPriority(CalendarElements type) {
		return calendarElementPriorityList.indexOf(type);
	}

	public Currency getCurrentCurrency() {
		return currentCurrency;
	}

	public Map<String, Double> getDefaultExchangeRates() {
		return defaultExchangeRates;
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
