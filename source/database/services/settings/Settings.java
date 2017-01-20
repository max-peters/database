package database.services.settings;

import java.util.HashMap;
import java.util.Map;
import database.plugin.element.Appointment;
import database.plugin.element.Birthday;
import database.plugin.element.CalendarElement;
import database.plugin.element.Day;
import database.plugin.element.Holiday;

public class Settings {
	public final int												eventDisplayRange	= 7;
	public final int												revertStackSize		= 1;
	public final String												storageDirectory	= System.getProperty("user.home") + "/Documents/storage1.xml";
	private final Map<Class<? extends CalendarElement>, Integer>	map;

	public Settings() {
		map = new HashMap<>();
		map.put(Appointment.class, 4);
		map.put(Birthday.class, 2);
		map.put(Day.class, 3);
		map.put(Holiday.class, 1);
	}

	public int getCalendarElementPriority(Class<CalendarElement> type) {
		return map.get(type);
	}
}
