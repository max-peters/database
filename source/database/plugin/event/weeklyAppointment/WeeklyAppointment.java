package database.plugin.event.weeklyAppointment;

import database.main.date.Date;
import database.main.date.Time;
import database.plugin.event.appointment.Appointment;

public class WeeklyAppointment extends Appointment {
	public WeeklyAppointment(String name, Date date, Time begin, Time end) {
		super(name, date, begin, end);
	}
}
