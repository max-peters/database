package database.plugin.event;

import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;

public abstract class Event extends Instance {
	public Event(Map<String, String> parameter) {
		super(parameter);
	}

	public Date getDate() {
		return new Date(getParameter("date"));
	}

	public String getName() {
		return getParameter("name");
	}

	protected abstract String output();

	protected Date updateYear() {
		Date localDate = null;
		Date currentDate = Date.getCurrentDate();
		if (currentDate.month.counter > getDate().month.counter || currentDate.month.counter == getDate().month.counter && currentDate.day.counter > getDate().day.counter) {
			localDate = new Date(getDate().day.counter + "." + getDate().month.counter + "." + String.valueOf(currentDate.year.counter + 1));
		}
		else {
			localDate = new Date(getDate().day.counter + "." + getDate().month.counter + "." + currentDate.year.counter);
		}
		return localDate;
	}
}
