package database.plugin.event.appointment;

import java.util.Map;
import database.main.date.Date;
import database.plugin.event.EventList;

public class AppointmentList extends EventList {
	@Override public void add(Map<String, String> map) {
		if (!new Date(map.get("date")).isPast()) {
			sortedAdd(new Appointment(map));
		}
	}
}
