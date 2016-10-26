package database.plugin.event.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import database.plugin.event.Event;

public class Appointment extends Event {
	public LocalTime	begin;
	public LocalTime	end;

	public Appointment(String name, LocalDate date, LocalTime begin, LocalTime end) {
		super(name, date);
		this.begin = begin;
		this.end = end;
	}

	@Override public LocalDate updateYear() {
		return date;
	}

	@Override protected String getAdditionToOutput(int year) {
		String string = "";
		if (begin != null) {
			string = "[" + begin + " UHR";
			if (end != null) {
				string += " to " + end + " UHR]";
			}
			else {
				string += "]";
			}
		}
		return string;
	}
}
