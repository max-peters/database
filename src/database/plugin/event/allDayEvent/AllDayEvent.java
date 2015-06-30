package database.plugin.event.allDayEvent;

import database.plugin.event.Event;

public class AllDayEvent extends Event {
	public AllDayEvent(String[] parameter, AllDayEventList list) {
		super(new String[] { parameter[0], parameter[1], "true" }, list);
	}

	public String toString() {
		return "allDayEvent : " + name + " / " + date.toString();
	}

	protected String output() {
		return updateYear().toString() + " - " + name;
	}
}
