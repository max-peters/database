package database.plugin.event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.plugin.Command;
import database.plugin.OutputFormatter;
import database.plugin.settings.Settings;

public class EventOutputFormatter extends OutputFormatter<Event> {
	private Settings settings;

	public EventOutputFormatter(Settings settings) {
		this.settings = settings;
	}

	@Command(tag = "all") public String printAll(Iterable<Event> iterable) throws BadLocationException {
		String output = "";
		for (String string : formatOutput(iterable)) {
			output += string + System.getProperty("line.separator");
		}
		return output;
	}

	protected List<String> formatOutput(Iterable<? extends Event> iterable) {
		List<String> output = new ArrayList<String>();
		int longestNameLength = 0;
		for (Event event : iterable) {
			if (event.updateYear().getYear() == LocalDate.now().getYear()) {
				if ((event.updateYear() + " - " + event.name).length() > longestNameLength) {
					longestNameLength = (event.updateYear().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + " - " + event.name).length();
				}
			}
		}
		for (Event event : iterable) {
			if (event.updateYear().getYear() == LocalDate.now().getYear()) {
				String line = event.updateYear().isEqual(LocalDate.now())	? "TODAY      - " + event.name
																			: event.updateYear().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + " - " + event.name;
				for (int i = line.length(); i < longestNameLength + 3; i++) {
					line += " ";
				}
				output.add(" " + line + event.getAdditionToOutput());
			}
		}
		return output;
	}

	@Override protected String getInitialOutput(Iterable<Event> iterable) {
		String output = "";
		for (String string : formatOutput(getNearEvents(iterable))) {
			output += string + System.getProperty("line.separator");
		}
		return output;
	}

	protected ArrayList<Event> getNearEvents(Iterable<? extends Event> iterable) {
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Event event : iterable) {
			if (ChronoUnit.DAYS.between(LocalDate.now(), event.updateYear()) <= settings.getDisplayedDays()) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}
}
