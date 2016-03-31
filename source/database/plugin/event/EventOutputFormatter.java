package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.date.Date;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.OutputFormatter;

public class EventOutputFormatter extends OutputFormatter<Event> {
	private ArrayList<EventPluginExtension<?>> extensionList;

	public void setExtensionList(ArrayList<EventPluginExtension<?>> extensionList) {
		this.extensionList = extensionList;
	}

	@Command(tag = "all") public String printAll(LinkedList<Event> list) throws BadLocationException {
		List<Event> outputList = new ArrayList<Event>();
		for (EventPluginExtension<?> extension : extensionList) {
			for (Event event : extension.getIterable()) {
				outputList.add(event);
			}
		}
		return sortedAndFormattedOutput(outputList);
	}

	@Override protected String getInitialOutput(LinkedList<Event> list) {
		List<Event> eventList = new ArrayList<Event>();
		for (EventPluginExtension<?> extension : extensionList) {
			for (Event event : getNearEvents(extension.getIterable())) {
				eventList.add(event);
			}
		}
		return sortedAndFormattedOutput(eventList);
	}

	private ArrayList<Event> getNearEvents(Iterable<? extends Event> iterable) {
		Date currentDate = Date.getCurrentDate();
		Date localDate;
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Event event : iterable) {
			localDate = event.updateYear();
			if (localDate.compareTo(currentDate) < 5) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}

	private String sortedAndFormattedOutput(List<Event> list) {
		String lines = "";
		int longestNameLength = 0;
		Collections.sort(list, new Comparator<Instance>() {
			@Override public int compare(Instance arg0, Instance arg1) {
				return ((Event) arg0).updateYear().compareTo(((Event) arg1).updateYear());
			}
		});
		for (Event event : list) {
			if (event.updateYear().year.counter == Date.getCurrentDate().year.counter) {
				if ((event.updateYear() + " - " + event.name).length() > longestNameLength) {
					longestNameLength = (event.updateYear() + " - " + event.name).length();
				}
			}
		}
		for (Event event : list) {
			if (event.updateYear().year.counter == Date.getCurrentDate().year.counter) {
				String line = event.updateYear().isToday() ? "TODAY      - " + event.name : event.updateYear() + " - " + event.name;
				for (int i = line.length(); i < longestNameLength + 3; i++) {
					line += " ";
				}
				lines += line + event.appendToOutput() + System.getProperty("line.separator");
			}
		}
		return lines;
	}
}
