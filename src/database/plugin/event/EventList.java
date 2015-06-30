package database.plugin.event;

import java.util.ArrayList;

import database.main.InstanceList;
import database.main.date.Date;

public abstract class EventList extends InstanceList {
	public ArrayList<Event> getNearEvents() {
		Date currentDate = Date.getDate();
		Date localDate;
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Object object : list) {
			Event event = (Event) object;
			localDate = event.updateYear();
			if ((localDate.compareTo(currentDate) < 3) && (localDate.compareTo(currentDate) >= 0)) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}

	public String initialOutput() {
		String output = "";
		for (Event event : getNearEvents()) {
			output = output + event.output() + "\r\n";
		}
		return output;
	}
}
