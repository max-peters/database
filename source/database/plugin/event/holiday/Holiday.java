package database.plugin.event.holiday;

import java.util.Map;
import database.main.date.Date;
import database.plugin.event.Event;

public class Holiday extends Event {
	public Holiday(Map<String, String> parameter) {
		super(parameter);
	}

	@Override public Date updateYear() {
		return date;
	}

	@Override protected String appendToOutput() {
		return "";
	}
}
