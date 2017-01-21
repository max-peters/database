package database.plugin.connector;

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
import database.plugin.element.Holiday;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;

public class HolidayURLConnector {
	public void getHolidays() throws BadLocationException, InterruptedException, SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		HolidayDatabaseConnector holidayConnector = (HolidayDatabaseConnector) ServiceRegistry.Instance().get(IConnectorRegistry.class).get(Holiday.class);
		List<String> lines = connectAndSetList();
		Iterable<Holiday> iterable;
		String temp;
		String name;
		LocalDate newDate;
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).matches(".*<a href=\"/Feiertage/feiertag_.*.html\" class=\"dash\">.*")) {
				iterable = holidayConnector.getList();
				temp = lines.get(i + 5).replace(" ", "");
				newDate = LocalDate.parse(temp.substring(0, temp.lastIndexOf(",")), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
				temp = lines.get(i + 1).replace("&ouml;", "ö").replace(":", " ");
				name = temp.substring(14, temp.length() - 1);
				if (!iterable.iterator().hasNext() && !newDate.isBefore(LocalDate.now())) {
					database.insert(new Holiday(name, newDate));
				}
				else {
					boolean contains = false;
					for (Holiday holiday : iterable) {
						if (holiday.getName().equals(name)) {
							contains = true;
							if (holiday.getDate().isBefore(newDate) && holiday.getDate().isBefore(LocalDate.now())) {
								database.insert(new Holiday(name, newDate));
								database.remove(holiday);
								break;
							}
						}
					}
					if (!contains && !newDate.isBefore(LocalDate.now())) {
						database.insert(new Holiday(name, newDate));
					}
				}
			}
		}
	}

	private List<String> connectAndSetList() {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String line;
		InputStreamReader isr;
		List<String> lines = new ArrayList<>();
		try {
			isr = new InputStreamReader(new URL("http://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html").openConnection().getInputStream());
			BufferedReader in = new BufferedReader(isr);
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		}
		catch (IOException e) {
			terminal.collectLine(" error 404: page not found", StringFormat.STANDARD, "holiday");
		}
		return lines;
	}
}
