package database.plugin.event;

import java.util.ArrayList;
import database.main.date.Date;
import database.plugin.InstanceList;

public abstract class EventList extends InstanceList {
	public ArrayList<Event> getNearEvents() {
		Date currentDate = Date.getCurrentDate();
		Date localDate;
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Object object : getList()) {
			Event event = (Event) object;
			localDate = event.updateYear();
			if (localDate.compareTo(currentDate) < 5) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}

	protected void sortedAdd(Event event) {
		int i = getList().size();
		while (i > 0 && ((Event) getList().get(i - 1)).getDate().compareTo(event.getDate()) > 0) {
			i--;
		}
		getList().add(i, event);
	}
}
