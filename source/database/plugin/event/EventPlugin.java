package database.plugin.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.text.BadLocationException;
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

public class EventPlugin extends InstancePlugin {
	private ArrayList<EventPluginExtention> extentionList;

	public EventPlugin(Storage storage) {
		super("event", null, storage);
		extentionList = new ArrayList<EventPluginExtention>();
		extentionList.add(new DayPlugin(storage));
		extentionList.add(new BirthdayPlugin(storage));
		extentionList.add(new HolidayPlugin(storage));
		extentionList.add(new AppointmentPlugin(storage));
	}

	@Override public void clearList() {
		for (InstancePlugin extention : extentionList) {
			extention.getInstanceList().clear();
		}
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		EventPluginExtention extention = chooseType();
		if (extention != null) {
			extention.createRequest();
			update();
		}
	}

	@Override public void display() {
		// nothing to display here
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = "";
		List<Event> eventList = new ArrayList<Event>();
		for (InstancePlugin extention : extentionList) {
			EventList currentEventList = (EventList) extention.getInstanceList();
			for (Event event : currentEventList.getNearEvents()) {
				eventList.add(event);
			}
		}
		Collections.sort(eventList, new Comparator<Instance>() {
			@Override public int compare(Instance arg0, Instance arg1) {
				return ((Event) arg0).updateYear().compareTo(((Event) arg1).updateYear());
			}
		});
		for (Event event : eventList) {
			initialOutput += event.output() + System.getProperty("line.separator");
		}
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(getIdentity() + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (InstancePlugin extention : extentionList) {
			list.addAll(extention.print());
		}
		list.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void read(PrintInformation pair) throws IOException {
		if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else {
			for (InstancePlugin extention : extentionList) {
				if (pair.getName().equals(extention.getIdentity())) {
					extention.create(pair.getMap());
				}
			}
		}
	}

	@Override public void remove(Instance toRemove) throws BadLocationException {
		for (InstancePlugin extention : extentionList) {
			extention.remove(toRemove);
		}
	}

	@Override public void show() {
		// nothing to show here
	}

	private EventPluginExtention chooseType() throws InterruptedException, BadLocationException {
		ArrayList<String> strings = new ArrayList<String>();
		EventPluginExtention toReturn = null;
		String pluginIdentity;
		int position;
		for (InstancePlugin extention : extentionList) {
			strings.add(extention.getIdentity());
		}
		strings.remove(2);
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (InstancePlugin extention : extentionList) {
				if (pluginIdentity.equals(extention.getIdentity())) {
					toReturn = (EventPluginExtention) extention;
				}
			}
		}
		return toReturn;
	}
}
