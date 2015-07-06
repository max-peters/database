package database.plugin.event;

import java.util.ArrayList;

import database.main.date.Date;
import database.plugin.InstanceList;

public class EventList extends InstanceList {
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

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		return null;
	}

	public String initialOutput() {
		return null;
	}

	public void add(String[] parameter) {
	}
}
