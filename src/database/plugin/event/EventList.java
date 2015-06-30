package database.plugin.event;

import java.util.ArrayList;

import database.main.InstanceList;
import database.main.date.Date;

public abstract class EventList extends InstanceList {
	public ArrayList<Event> getNearEvents() {
		Date currentDate = Date.getDate();
		Date localDate;
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Object object : getList()) {
			Event event = (Event) object;
			localDate = event.updateYear();
			if ((currentDate.calculateDifference(localDate) < 3) && (currentDate.calculateDifference(localDate) >= 0)) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}
}
