package database.plugin.event.multiDayAppointment;

import java.time.LocalDate;
import java.time.LocalTime;
import database.plugin.event.appointment.Appointment;

public class MultiDayAppointment extends Appointment {
	public LocalDate lastDay;

	public MultiDayAppointment(String name, LocalDate firstDay, LocalTime begin, LocalDate lastDay, LocalTime end) {
		super(name, firstDay, begin, end);
		this.lastDay = lastDay;
	}
}