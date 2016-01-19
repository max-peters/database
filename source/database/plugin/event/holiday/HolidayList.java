package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.event.EventList;

public class HolidayList extends EventList {
	URLConnection	connection;
	List<String>	lines	= new ArrayList<String>();
	URL				url;

	@Override public void add(Map<String, String> parameter) throws IOException {
		url = new URL("http://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html");
		if (testURL() && lines.isEmpty()) {
			prepareList();
		}
		if (new Date(parameter.get("date")).isPast()) {
			if (!lines.isEmpty()) {
				getHolidays();
			}
			else {
				sortedAdd(new Holiday(parameter));
			}
		}
		else if (!containsEquals(parameter)) {
			sortedAdd(new Holiday(parameter));
		}
	}

	public void getHolidays() {
		Map<String, String> map;
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
				if (getList().isEmpty() && !newDate.isPast()) {
					sortedAdd(new Holiday(map));
				}
				else {
					boolean contains = false;
					for (Instance instance : getList()) {
						Holiday holiday = (Holiday) instance;
						if (holiday.getName().equals(name)) {
							contains = true;
							if (holiday.getDate().compareTo(newDate) < 0 && holiday.getDate().isPast()) {
								holiday.getParameter().replace("date", date);
								return;
							}
						}
					}
					if (!contains && !newDate.isPast()) {
						sortedAdd(new Holiday(map));
					}
				}
			}
		}
	}

	private void prepareList() throws IOException {
		String line;
		lines.clear();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();
	}

	private boolean testURL() {
		try {
			connection = url.openConnection();
			return true;
		}
		catch (Throwable e) {
			return false;
		}
	}
}
