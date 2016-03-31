package database.plugin.event.appointment;

import database.plugin.event.EventList;

public class AppointmentList extends EventList<Appointment> {
	@Override public boolean add(Appointment appointment) {
		if (appointment.date.isPast()) {
			return false;
		}
		return super.add(appointment);
	}
}
