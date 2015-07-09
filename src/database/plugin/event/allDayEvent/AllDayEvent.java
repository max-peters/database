package database.plugin.event.allDayEvent;

import database.plugin.event.Event;

public class AllDayEvent extends Event {
	public AllDayEvent(String[][] parameter, AllDayEventList list) {
		super(new String[][] { parameter[0], parameter[1], { "everyYear", "true" } }, list);
	}

	@Override public String[][] getParameter() {
		return new String[][] { { "type", "all day event" }, { "name", getParameter("name") }, { "date", getParameter("date") } };
	}

	@Override protected String output() {
		return updateYear().toString() + " - " + getParameter("name");
	}
}
