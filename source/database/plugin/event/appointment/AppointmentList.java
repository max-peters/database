package database.plugin.event.appointment;

import java.util.Map;
import database.main.date.Date;
import database.plugin.event.EventList;

public class AppointmentList extends EventList {
	@Override public void add(Map<String, String> parameter) {
		if (!new Date(parameter.get("date")).isPast()) {
			sortedAdd(new Appointment(parameter));
		}
	}
}
