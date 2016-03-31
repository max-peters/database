package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.event.EventList;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class HolidayPlugin extends EventPluginExtension<Holiday> {
	private List<String> lines = new ArrayList<String>();

	public HolidayPlugin(Storage storage) {
		super("holiday", new HolidayList(), storage);
	}

	@Override public Holiday create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
																InvocationTargetException, NoSuchMethodException, SecurityException {
		if (lines.isEmpty()) {
			prepareList();
		}
		if (new Date(map.get("date")).isPast()) {
			if (!lines.isEmpty()) {
				getHolidays();
			}
			else {
				return new Holiday(map);
			}
		}
		else if (!getInstanceList().contains(map)) {
			return new Holiday(map);
		}
		return null;
	}

	private void getHolidays()	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
								SecurityException {
		Map<String, String> map;
		EventList<Holiday> list = (EventList<Holiday>) getInstanceList();
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).matches(".*<a href=\"/Feiertage/feiertag_.*.html\" class=\"dash\">.*")) {
				map = new HashMap<String, String>();
				String temp = lines.get(i + 5).replace(" ", "");
				String date = temp.substring(0, temp.lastIndexOf(","));
				temp = lines.get(i + 1).replace("&ouml;", "ö").replace(":", " ");
				String name = temp.substring(14, temp.length() - 1);
				Date newDate = new Date(date);
				map.put("name", name);
				map.put("date", date);
				if (list.isEmpty() && !newDate.isPast()) {
					list.add(new Holiday(map));
				}
				else {
					boolean contains = false;
					for (Instance instance : list) {
						Holiday holiday = (Holiday) instance;
						if (holiday.name.equals(name)) {
							contains = true;
							if (holiday.date.compareTo(newDate) < 0 && holiday.date.isPast()) {
								holiday.date = newDate;
								return;
							}
						}
					}
					if (!contains && !newDate.isPast()) {
						list.add(new Holiday(map));
					}
				}
			}
		}
	}

	private void prepareList() throws IOException {
		String line;
		lines.clear();
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html")	.openConnection()
																																						.getInputStream()));
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();
	}
}
