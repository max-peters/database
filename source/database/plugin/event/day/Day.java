package database.plugin.event.day;

import java.util.Map;
import database.plugin.event.Event;

public class Day extends Event {
	public Day(Map<String, String> parameter) {
		super(parameter);
	}

	@Override protected String appendToOutput() {
		return "(" + date.year + ")";
	}
}