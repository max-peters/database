package database.plugin.event.allDayEvent;

import database.plugin.event.Event;

public class AllDayEvent extends Event {
	public AllDayEvent(String[][] parameter, AllDayEventList list) {
		super(new String[][] { parameter[0], parameter[1] }, list);
	}

	@Override protected String output() {
		return updateYear().toString() + " - " + getName() + " (" + getDate().year + ")";
	}
}
