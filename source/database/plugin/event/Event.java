package database.plugin.event;

import java.util.Map;

import database.main.date.Date;
import database.plugin.Instance;

public abstract class Event extends Instance {
	public Event(Map<String, String> parameter, EventList list) {
		super(parameter, list);
	}

	@Override public int compareTo(Instance instance) {
		if (instance instanceof Event) {
			return getDate().compareTo(((Event) instance).getDate());
		}
		else {
			return 0;
		}
	}

	protected Date getDate() {
		return new Date(getParameter("date"));
	}

	protected String getName() {
		return getParameter("name");
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

	protected abstract String output();
}
