package database.plugin.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.date.Date;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.PrintInformation;
import database.plugin.event.appointment.AppointmentPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.day.DayPlugin;
import database.plugin.event.holiday.HolidayPlugin;
import database.plugin.storage.Storage;

public class EventPlugin extends InstancePlugin<Event> {
	private ArrayList<EventPluginExtension<?>> extensionList;

	public EventPlugin(Storage storage) {
		super("event", null, storage);
		extensionList = new ArrayList<EventPluginExtension<?>>();
		extensionList.add(new DayPlugin(storage));
		extensionList.add(new BirthdayPlugin(storage));
		extensionList.add(new HolidayPlugin(storage));
		extensionList.add(new AppointmentPlugin(storage));
	}

	@Override public void clearList() {
		for (EventPluginExtension<?> extension : extensionList) {
			extension.getInstanceList().clear();
		}
	}

	@Override public Event create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
															NoSuchMethodException, SecurityException {
		throw new RuntimeException("no event instatioationdasfda");
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		EventPluginExtension<?> extension = chooseType();
		if (extension != null) {
			extension.createRequest();
			update();
		}
	}

	@Override public void display() {
		// nothing to display here
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput;
		List<Event> eventList = new ArrayList<Event>();
		for (EventPluginExtension<?> extension : extensionList) {
			EventList<?> currentEventList = (EventList<?>) extension.getInstanceList();
			for (Event event : currentEventList.getNearEvents()) {
				eventList.add(event);
			}
		}
		initialOutput = sortedAndFormattedOutput(eventList);
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(getIdentity() + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (EventPluginExtension<?> extension : extensionList) {
			list.addAll(extension.print());
		}
		list.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void read(PrintInformation pair)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException {
		if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else {
			for (EventPluginExtension<?> extension : extensionList) {
				if (pair.getName().equals(extension.getIdentity())) {
					extension.createAndAdd(pair.getMap());
				}
			}
		}
	}

	@Override public void remove(Instance toRemove) throws BadLocationException {
		for (EventPluginExtension<?> extension : extensionList) {
			extension.remove(toRemove);
		}
	}

	@Override @Command(tag = "show") public void show() throws BadLocationException, InterruptedException {
		boolean display = getDisplay();
		Terminal.request("show", "(all)");
		setDisplay(false);
		Terminal.update();
		List<Event> list = new ArrayList<Event>();
		for (EventPluginExtension<?> extension : extensionList) {
			for (Instance instance : extension.getInstanceList()) {
				list.add((Event) instance);
			}
		}
		Terminal.getLineOfCharacters('-');
		Terminal.printLine(sortedAndFormattedOutput(list), StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
		setDisplay(display);
		Terminal.update();
	}

	protected List<PrintInformation> extensionPrint() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (Instance instance : getInstanceList()) {
			list.add(new PrintInformation(getIdentity(), instance.getParameter()));
		}
		return list;
	}

	private EventPluginExtension<?> chooseType() throws InterruptedException, BadLocationException {
		ArrayList<String> strings = new ArrayList<String>();
		EventPluginExtension<?> toReturn = null;
		String pluginIdentity;
		int position;
		for (EventPluginExtension<?> extension : extensionList) {
			strings.add(extension.getIdentity());
		}
		strings.remove(2);
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (EventPluginExtension<?> extension : extensionList) {
				if (pluginIdentity.equals(extension.getIdentity())) {
					toReturn = extension;
				}
			}
		}
		return toReturn;
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
