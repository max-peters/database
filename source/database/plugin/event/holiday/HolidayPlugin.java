package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.main.date.Date;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class HolidayPlugin extends EventPluginExtension<Holiday> {
	public HolidayPlugin(Storage storage) {
		super("holiday", storage);
	}

	@Override public Holiday create(Map<String, String> parameter) {
		return new Holiday(parameter.get("name"), new Date(parameter.get("date")));
	}

	public void updateHolidays() throws IOException {
		for (Holiday holiday : getIterable()) {
			if (holiday.date.isPast()) {
				getHolidays();
			}
		}
	}

	private void getHolidays() throws IOException {
		Map<String, String> map;
		List<String> lines = getList();
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
					createAndAdd(map);
				}
				else {
					boolean contains = false;
					for (Holiday holiday : getIterable()) {
						if (holiday.name.equals(name)) {
							contains = true;
							if (holiday.date.compareTo(newDate) < 0 && holiday.date.isPast()) {
								holiday.date = newDate;
								return;
							}
						}
					}
					if (!contains && !newDate.isPast()) {
						createAndAdd(map);
					}
				}
			}
		}
	}

	private List<String> getList() throws IOException {
		List<String> lines = new ArrayList<String>();
		String line;
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(new URL("http://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html").openConnection().getInputStream());
		}
		catch (IOException e) {
			return lines;
		}
		BufferedReader in = new BufferedReader(isr);
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();
		return lines;
	}
}
