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
import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class HolidayPlugin extends EventPluginExtention {
	private List<String> lines = new ArrayList<String>();

	public HolidayPlugin(Storage storage) {
		super("holiday", new HolidayList(), storage);
	}

	@Override public void create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
															NoSuchMethodException, SecurityException {
		EventList list = (EventList) getInstanceList();
		if (new Date(map.get("date")).isPast()) {
			if (lines.isEmpty()) {
				prepareList();
				getHolidays();
			}
			else {
				list.add(map);
			}
		}
		else if (!list.contains(map)) {
			list.add(map);
		}
	}

	private void getHolidays()	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
								SecurityException {
		Map<String, String> map;
		EventList list = (EventList) getInstanceList();
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
					list.add(map);
				}
				else {
					boolean contains = false;
					for (Instance instance : list.getIterable()) {
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
						list.add(map);
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
