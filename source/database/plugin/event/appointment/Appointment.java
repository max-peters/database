package database.plugin.event.appointment;

import java.util.Map;
import database.main.date.Date;
import database.plugin.event.Event;

public class Appointment extends Event {
	public Appointment(Map<String, String> parameter) {
		super(parameter);
	}

	@Override protected String output() {
		return date + " - " + name;
	}

	@Override protected Date updateYear() {
		return date;
	}
}
