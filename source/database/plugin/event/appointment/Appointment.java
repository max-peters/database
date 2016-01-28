package database.plugin.event.appointment;

import java.util.HashMap;
import java.util.Map;
import database.main.date.Date;
import database.plugin.event.Event;

public class Appointment extends Event {
	String attribute;

	public Appointment(Map<String, String> parameter) {
		super(parameter);
		attribute = parameter.get("attribute");
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("name", name);
		parameter.put("date", date.toString());
		parameter.put("attribute", attribute);
		return parameter;
	}

	@Override public Date updateYear() {
		return date;
	}

	@Override protected String appendToOutput() {
		if (attribute.isEmpty()) {
			return "";
		}
		else {
			return "[" + attribute + "]";
		}
	}
}
