package database.plugin.event;

import database.main.Instance;
import database.main.date.Date;

public abstract class Event extends Instance implements Comparable<Event> {
	protected Date		date;
	protected String	name;
	protected boolean	eachYear;

	public Event(String[] parameter, EventList list) {
		super(parameter[0], list);
		this.name = parameter[0];
		this.date = new Date(parameter[1]);
		this.eachYear = Boolean.valueOf(parameter[2]);
	}

	public int compareTo(Event event) {
		return date.compareTo(event.date);
	}

	protected Date updateYear() {
		Date localDate = null;
		Date currentDate = Date.getDate();
		if (currentDate.month.counter > date.month.counter) {
			int nextYear = currentDate.year.counter + 1;
			localDate = new Date(date.day.counter + "." + date.month.counter + "." + nextYear);
		}
		else {
			localDate = new Date(date.day.counter + "." + date.month.counter + "." + currentDate.year.counter);
		}
		return localDate;
	}

	protected abstract String output();
}
