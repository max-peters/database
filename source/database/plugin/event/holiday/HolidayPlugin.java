package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.Terminal;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class HolidayPlugin extends EventPluginExtension<Holiday> {
	public HolidayPlugin(Storage storage) {
		super("holiday", storage, Holiday.class);
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		// no create request
	}

	public void updateHolidays() throws BadLocationException, InterruptedException {
		try {
			if (!getIterable().iterator().hasNext()) {
				getHolidays();
			}
			for (Holiday holiday : getIterable()) {
				if (holiday.date.isBefore(LocalDate.now())) {
					getHolidays();
					return;
				}
			}
		}
		catch (IOException e) {
			Terminal.collectLine("couldn't update holidays", StringFormat.STANDARD);
		}
	}

	private void getHolidays() throws IOException, BadLocationException, InterruptedException {
		List<String> lines = getList();
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).matches(".*<a href=\"/Feiertage/feiertag_.*.html\" class=\"dash\">.*")) {
				String temp = lines.get(i + 5).replace(" ", "");
				LocalDate newDate = LocalDate.parse(temp.substring(0, temp.lastIndexOf(",")), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
				temp = lines.get(i + 1).replace("&ouml;", "ö").replace(":", " ");
				String name = temp.substring(14, temp.length() - 1);
				if (list.isEmpty() && !newDate.isBefore(LocalDate.now())) {
					add(new Holiday(name, newDate));
				}
				else {
					boolean contains = false;
					for (Holiday holiday : getIterable()) {
						if (holiday.name.equals(name)) {
							contains = true;
							if (holiday.date.isBefore(newDate) && holiday.date.isBefore(LocalDate.now())) {
								add(new Holiday(name, newDate));
								remove(holiday);
								return;
							}
						}
					}
					if (!contains && !newDate.isBefore(LocalDate.now())) {
						add(new Holiday(name, newDate));
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
