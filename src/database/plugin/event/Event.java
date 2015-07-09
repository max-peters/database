package database.plugin.event;

import database.main.date.Date;
import database.plugin.Instance;

public abstract class Event extends Instance implements Comparable<Event> {
	public Event(String[][] parameter, EventList list) {
		super(parameter, parameter[0][1], list);
	}

	@Override public int compareTo(Event event) {
		return new Date(getParameter("date")).compareTo(new Date(event.getParameter("date")));
	}

	protected Date updateYear() {
		Date localDate = null;
		Date currentDate = Date.getDate();
		if (currentDate.month.counter > new Date(getParameter("date")).month.counter) {
			int nextYear = currentDate.year.counter + 1;
			localDate = new Date(new Date(getParameter("date")).day.counter + "." + new Date(getParameter("date")).month.counter + "." + nextYear);
		}
		else {
			localDate = new Date(new Date(getParameter("date")).day.counter + "." + new Date(getParameter("date")).month.counter + "." + currentDate.year.counter);
		}
		return localDate;
	}

	protected abstract String output();
}
