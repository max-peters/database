package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.date.Date;
import database.main.date.Time;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.OutputFormatter;
import database.plugin.event.appointment.Appointment;
import database.plugin.settings.Settings;

public class EventOutputFormatter extends OutputFormatter<Event> {
	private Settings settings;

	public EventOutputFormatter(Settings settings) {
		this.settings = settings;
	}

	@Command(tag = "all") public String printAll(Iterable<Event> iterable) throws BadLocationException {
		List<Event> eventList = new ArrayList<Event>();
		for (Event event : iterable) {
			eventList.add(event);
		}
		return sortedAndFormattedOutput(eventList);
	}

	@Override protected String getInitialOutput(Iterable<Event> iterable) {
		List<Event> eventList = new ArrayList<Event>();
		for (Event event : iterable) {
			eventList.add(event);
		}
		return sortedAndFormattedOutput(getNearEvents(eventList));
	}

	private ArrayList<Event> getNearEvents(Iterable<? extends Event> iterable) {
		Date currentDate = Date.getCurrentDate();
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Event event : iterable) {
			if (event.updateYear().compareTo(currentDate) <= settings.getDisplayedDays()) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}

	private String sortedAndFormattedOutput(List<Event> list) {
		String lines = "";
		int longestNameLength = 0;
		List<Appointment> appointmentList = new ArrayList<Appointment>();
		for (Event event : list) {
			if (event instanceof Appointment) {
				appointmentList.add((Appointment) event);
			}
		}
		for (Appointment appointment : appointmentList) {
			list.remove(appointment);
		}
		Collections.sort(appointmentList, new Comparator<Appointment>() {
			@Override public int compare(Appointment o1, Appointment o2) {
				Time temp1 = o1.begin != null ? o1.begin : new Time("00:00");
				Time temp2 = o2.begin != null ? o2.begin : new Time("00:00");
				return temp2.compareTo(temp1);
			}
		});
		list.addAll(appointmentList);
		Collections.sort(list, new Comparator<Instance>() {
			@Override public int compare(Instance arg0, Instance arg1) {
				return ((Event) arg0).updateYear().compareTo(((Event) arg1).updateYear());
			}
		});
		for (Event event : list) {
			if (event.updateYear().year.counter == Date.getCurrentDate().year.counter) {
				if ((event.updateYear() + " - " + event.name).length() > longestNameLength) {
					longestNameLength = (event.updateYear() + " - " + event.name).length();
				}
			}
		}
		for (Event event : list) {
			if (event.updateYear().year.counter == Date.getCurrentDate().year.counter) {
				String line = event.updateYear().isToday() ? "TODAY      - " + event.name : event.updateYear() + " - " + event.name;
				for (int i = line.length(); i < longestNameLength + 3; i++) {
					line += " ";
				}
				lines += " " + line + event.appendToOutput() + System.getProperty("line.separator");
			}
		}
		return lines;
	}
}
