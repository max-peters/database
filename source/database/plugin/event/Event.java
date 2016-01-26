package database.plugin.event;

import java.util.HashMap;
import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;

public abstract class Event extends Instance {
	public Date		date;
	public String	name;

	public Event(Map<String, String> parameter) {
		this.date = new Date(parameter.get("date"));
		this.name = parameter.get("name");
	}

	protected abstract String output();

	protected Date updateYear() {
		Date localDate = null;
		Date currentDate = Date.getCurrentDate();
		if (currentDate.month.counter > date.month.counter || currentDate.month.counter == date.month.counter && currentDate.day.counter > date.day.counter) {
			localDate = new Date(date.day.counter + "." + date.month.counter + "." + String.valueOf(currentDate.year.counter + 1));
		}
		else {
			localDate = new Date(date.day.counter + "." + date.month.counter + "." + currentDate.year.counter);
		}
		return localDate;
	}

	@Override public boolean equals(Object object) {
		Event event;
		if (object != null && object.getClass().equals(this.getClass())) {
			event = (Event) object;
			if (event.date.equals(date) && event.name.equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("name", name);
		parameter.put("date", date.toString());
		return parameter;
	}
}
