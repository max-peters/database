package database.plugin.event.allDayEvent;

import java.util.Map;

import database.plugin.event.Event;

public class AllDayEvent extends Event {
	public AllDayEvent(Map<String, String> parameter, AllDayEventList list) {
		super(parameter, list);
	}

	@Override protected String output() {
		return updateYear().toString() + " - " + getName() + " (" + getDate().year + ")";
	}
}
