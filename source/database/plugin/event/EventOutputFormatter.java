package database.plugin.event;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.date.Date;
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

	@Override protected String getInitialOutput(Iterable<Event> iterable) {
		String output = "";
		for (String string : formatOutput(getNearEvents(iterable))) {
			output += string + System.getProperty("line.separator");
		}
		return output;
	}

	protected ArrayList<Event> getNearEvents(Iterable<? extends Event> iterable) {
		Date currentDate = Date.getCurrentDate();
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Event event : iterable) {
			if (event.updateYear().compareTo(currentDate) <= settings.getDisplayedDays()) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}

	protected List<String> formatOutput(Iterable<? extends Event> iterable) {
		List<String> output = new ArrayList<String>();
		int longestNameLength = 0;
		for (Event event : iterable) {
			if (event.updateYear().year.counter == Date.getCurrentDate().year.counter) {
				if ((event.updateYear() + " - " + event.name).length() > longestNameLength) {
					longestNameLength = (event.updateYear() + " - " + event.name).length();
				}
			}
		}
		for (Event event : iterable) {
			if (event.updateYear().year.counter == Date.getCurrentDate().year.counter) {
				String line = event.updateYear().isToday() ? "TODAY      - " + event.name : event.updateYear() + " - " + event.name;
				for (int i = line.length(); i < longestNameLength + 3; i++) {
					line += " ";
				}
				output.add(" " + line + event.getAdditionToOutput());
			}
		}
		return output;
	}
}
