package database.plugin.event.weeklyAppointment;

import java.time.LocalDate;
import java.time.LocalTime;
import database.plugin.event.appointment.Appointment;

public class WeeklyAppointment extends Appointment {
	public WeeklyAppointment(String name, LocalDate date, LocalTime begin, LocalTime end) {
		super(name, date, begin, end);
	}
}
