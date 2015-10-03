package database.plugin.event.holiday;

import java.util.Map;

import database.plugin.event.Event;
import database.plugin.event.EventList;

public class Holiday extends Event {
	public Holiday(Map<String, String> parameter, EventList list) {
		super(parameter, list);
	}

	@Override protected String output() {
		return getDate() + " - " + getName();
	}
}
