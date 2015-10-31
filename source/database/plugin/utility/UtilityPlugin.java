package database.plugin.utility;

import java.util.HashMap;
import java.util.Map;
import database.main.Terminal;
import database.main.date.Date;
import database.plugin.Command;
import database.plugin.Plugin;

public class UtilityPlugin extends Plugin {
	public UtilityPlugin() {
		super("utility");
	}

	@Command(tag = "days") public void calculateDayNumber() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("first date", null);
		map.put("second date", null);
		request(map);
		Date firstDate = new Date(map.get("first date"));
		Date secondDate = new Date(map.get("second date"));
		Terminal.printLine("day number between " + firstDate + " and " + secondDate + ":");
		Terminal.printLine(Math.abs(firstDate.compareTo(secondDate)));
		Terminal.waitForInput();
	}
}
