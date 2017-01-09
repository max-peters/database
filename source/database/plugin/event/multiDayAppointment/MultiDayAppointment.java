package database.plugin.event.multiDayAppointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import database.plugin.event.appointment.Appointment;

public class MultiDayAppointment extends Appointment {
	public LocalDate lastDay;

	public MultiDayAppointment(String name, LocalDate firstDay, LocalTime begin, LocalDate lastDay, LocalTime end) {
		super(name, firstDay, begin, end);
		this.lastDay = lastDay;
	}

	@Override protected String getAdditionToOutput() {
		String string = "";
		if (begin != null) {
			string = "[" + begin + " UHR ";
		}
		else {
			string = "[";
		}
		string += "until " + lastDay.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		if (end != null) {
			string += " " + end + " UHR]";
		}
		else {
			string += "]";
		}
		return string;
	}
}