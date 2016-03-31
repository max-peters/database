package database.plugin.event;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import database.main.date.Date;
import database.plugin.Instance;

public abstract class Event extends Instance {
	public Date		date;
	public String	name;

	public Event(String name, Date date) {
		this.name = name;
		this.date = date;
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

	@Override public void insertParameter(Element element) {
		element.setAttribute("name", name);
		element.setAttribute("date", date.toString());
	}

	public Date updateYear() {
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

	protected abstract String appendToOutput();
}
