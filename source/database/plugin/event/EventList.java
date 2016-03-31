package database.plugin.event;

import java.util.ArrayList;
import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.InstanceList;

public abstract class EventList<T extends Event> extends InstanceList<T> {
	@Override public boolean add(T event) {
		int i = size();
		while (i > 0 && get(i - 1).date.compareTo(event.date) > 0) {
			i--;
		}
		add(i, event);
		return true;
	}

	public ArrayList<Event> getNearEvents() {
		Date currentDate = Date.getCurrentDate();
		Date localDate;
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Instance instance : this) {
			Event event = (Event) instance;
			localDate = event.updateYear();
			if (localDate.compareTo(currentDate) < 5) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}
}
