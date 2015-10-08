package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.event.EventList;

public class HolidayList extends EventList {
	@Override public void add(Map<String, String> parameter) throws IOException {
		if (getList().isEmpty() && new Date(parameter.get("date")).compareTo(Date.getCurrentDate()) >= 0) {
			sortedAdd(new Holiday(parameter, this));
		}
		else {
			getHolidays();
			boolean contains = false;
			for (Instance instance : getList()) {
				Holiday holiday = (Holiday) instance;
				if (holiday.getName().equals(parameter.get("name"))) {
					contains = true;
					if (holiday.getDate().compareTo(new Date(parameter.get("date"))) < 0 && holiday.getDate().isPast()) {
						holiday.getParameter().replace("date", parameter.get("date"));
						return;
					}
				}
			}
			if (!contains && new Date(parameter.get("date")).compareTo(Date.getCurrentDate()) >= 0) {
				sortedAdd(new Holiday(parameter, this));
			}
		}
	}

	private void getHolidays() throws IOException {
		System.out.println("hiu");
		int lineCounter = 0;
		String line;
		String[] lines;
		Map<String, String> map;
		URL url = new URL("http://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html");
		URLConnection conn = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = in.readLine()) != null) {
			lineCounter++;
		}
		lines = new String[lineCounter];
		lineCounter = 0;
		in.close();
		conn = url.openConnection();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = in.readLine()) != null) {
			lines[lineCounter++] = line;
		}
		in.close();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].matches(".*<a href=\"/Feiertage/feiertag_.*.html\" class=\"dash\">.*")) {
				map = new HashMap<String, String>();
				String temp = lines[i + 5].replace(" ", "");
				String date = temp.substring(0, temp.lastIndexOf(","));
				temp = lines[i + 1].replace("&ouml;", "ö").replace(":", " ");
				String name = temp.substring(14, temp.length() - 1);
				map.put("name", name);
				map.put("date", date);
				add(map);
			}
		}
	}
}
