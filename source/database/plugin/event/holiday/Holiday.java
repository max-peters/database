package database.plugin.event.holiday;

import java.util.Map;

import database.plugin.event.Event;

public class Holiday extends Event {
	public Holiday(Map<String, String> parameter) {
		super(parameter);
	}

	@Override protected String output() {
		return getDate() + " - " + getName();
	}
}
