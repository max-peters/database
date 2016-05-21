package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class HolidayPlugin extends EventPluginExtension<Holiday> {
	public HolidayPlugin(Storage storage, Backup backup) {
		super("holiday", storage, backup, Holiday.class);
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		// no create request
	}

	public void updateHolidays() throws IOException {
		for (Holiday holiday : getIterable()) {
			if (holiday.date.isBefore(LocalDate.now())) {
				getHolidays();
				return;
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
				LocalDate newDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
				map.put("name", name);
				map.put("date", date);
				if (list.isEmpty() && !newDate.isBefore(LocalDate.now())) {
					add(new Holiday(name, LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.uuuu"))));
				}
				else {
					boolean contains = false;
					for (Holiday holiday : getIterable()) {
						if (holiday.name.equals(name)) {
							contains = true;
							if (holiday.date.isBefore(newDate) && holiday.date.isBefore(LocalDate.now())) {
								holiday.date = newDate;
								return;
							}
						}
					}
					if (!contains && !newDate.isBefore(LocalDate.now())) {
						add(new Holiday(name, LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.uuuu"))));
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
