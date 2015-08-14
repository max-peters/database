package database.plugin.event;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.main.date.Date;
import database.plugin.Instance;

public abstract class Event extends Instance implements Comparable<Event> {
	public Event(String[][] parameter, EventList list) {
		super(parameter, parameter[0][1], list);
	}

	@Override public int compareTo(Event event) {
		return getDate().compareTo(event.getDate());
	}

	protected Date updateYear() {
		Date localDate = null;
		Date currentDate = Date.getDate();
		if (currentDate.month.counter > getDate().month.counter || (currentDate.month.counter == getDate().month.counter && currentDate.day.counter > getDate().day.counter)) {
			localDate = new Date(getDate().day.counter + "." + getDate().month.counter + "." + String.valueOf(currentDate.year.counter + 1));
		}
		else {
			localDate = new Date(getDate().day.counter + "." + getDate().month.counter + "." + currentDate.year.counter);
		}
		return localDate;
	}

	@Getter protected Date getDate() {
		return new Date(getParameter("date"));
	}

	@Getter protected String getName() {
		return getParameter("name");
	}

	protected abstract String output();
}
