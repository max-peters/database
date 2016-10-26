package database.plugin.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import database.plugin.Instance;
import database.plugin.event.appointment.Appointment;

public abstract class Event extends Instance implements Comparable<Event> {
	public LocalDate	date;
	public String		name;

	public Event(String name, LocalDate date) {
		this.name = name;
		this.date = date;
	}

	@Override public int compareTo(Event event) {
		LocalDateTime e1;
		LocalDateTime e2;
		if (this instanceof Appointment) {
			if (((Appointment) this).begin != null) {
				e1 = date.atTime(((Appointment) this).begin);
			}
			else {
				e1 = updateYear().atTime(0, 0);
			}
		}
		else {
			e1 = updateYear().atTime(23, 59);
		}
		if (event instanceof Appointment) {
			if (((Appointment) event).begin != null) {
				e2 = event.date.atTime(((Appointment) event).begin);
			}
			else {
				e2 = event.updateYear().atTime(0, 0);
			}
		}
		else {
			e2 = event.updateYear().atTime(23, 59);
		}
		return e1.compareTo(e2);
	}

	public abstract LocalDate updateYear();

	protected abstract String getAdditionToOutput(int year);
}
