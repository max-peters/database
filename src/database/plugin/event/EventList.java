package database.plugin.event;

import java.util.ArrayList;

import database.main.date.Date;
import database.plugin.InstanceList;

public abstract class EventList extends InstanceList {
	@Override public void add(String[][] parameter) {
	}

	public ArrayList<Event> getNearEvents() {
		Date currentDate = Date.getDate();
		Date localDate;
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Object object : getList()) {
			Event event = (Event) object;
			localDate = event.updateYear();
			if (localDate.compareTo(currentDate) < 3) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}
}
