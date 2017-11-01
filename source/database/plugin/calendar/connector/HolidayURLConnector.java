package database.plugin.calendar.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.plugin.calendar.element.Holiday;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;

public class HolidayURLConnector {
	public void getHolidays() throws BadLocationException, InterruptedException, SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		HolidayDatabaseConnector holidayConnector = (HolidayDatabaseConnector) ServiceRegistry.Instance()
				.get(IConnectorRegistry.class).get(Holiday.class);
		List<String> lines = connectAndSetList();
		Iterable<Holiday> iterable = holidayConnector.getList();
		String temp;
		String name;
		LocalDate date;
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).trim().matches("<a href=\"/deutschland/feiertage/.*/\" class=\"dash\">")) {
				name = lines.get(i + 1).trim().replace(":", "").replace("&ouml;", "\u00F6");
				temp = lines.get(i + 5).trim();
				date = LocalDate.parse(temp.substring(0, temp.lastIndexOf(",")),
						DateTimeFormatter.ofPattern("dd.MM.uuuu"));
				if (!iterable.iterator().hasNext() && !date.isBefore(LocalDate.now())) {
					database.insert(new Holiday(name, date));
				}
				else {
					boolean contains = false;
					for (Holiday holiday : iterable) {
						if (holiday.getName().equals(name)) {
							contains = true;
							if (holiday.getDate().isBefore(date) && holiday.getDate().isBefore(LocalDate.now())) {
								database.insert(new Holiday(name, date));
								database.remove(holiday);
								iterable = holidayConnector.getList();
								break;
							}
						}
					}
					if (!contains && !date.isBefore(LocalDate.now())) {
						database.insert(new Holiday(name, date));
						iterable = holidayConnector.getList();
					}
				}
			}
		}
	}

	private List<String> connectAndSetList() {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String line;
		List<String> lines = new ArrayList<>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new URL("https://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html").openStream()));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		}
		catch (IOException e) {}
		if (lines.isEmpty()) {
			terminal.collectLine(" error 404: page not found", StringFormat.STANDARD, "holiday");
		}
		return lines;
	}
}
