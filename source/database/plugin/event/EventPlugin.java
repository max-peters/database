package database.plugin.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
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
		super("event", storage, new EventOutputFormatter());
		extensionList = new ArrayList<EventPluginExtension<?>>();
		extensionList.add(new DayPlugin(storage));
		extensionList.add(new BirthdayPlugin(storage));
		extensionList.add(new HolidayPlugin(storage));
		extensionList.add(new AppointmentPlugin(storage));
		((EventOutputFormatter) formatter).setExtensionList(extensionList);
	}

	@Override public void add(Event event) {
		throw new RuntimeException("event plugin add attempt");
	}

	@Override public void clearList() {
		for (EventPluginExtension<?> extension : extensionList) {
			extension.clearList();
		}
	}

	@Override public Event create(Map<String, String> map) {
		throw new RuntimeException("event plugin create attempt");
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		EventPluginExtension<?> extension = chooseType();
		if (extension != null) {
			extension.createRequest();
			update();
		}
	}

	@Override public void display() {
		// nothing to display here
	}

	@Override public Iterable<Event> getIterable() {
		throw new RuntimeException("event plugin getIterable attempt");
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (EventPluginExtension<?> extension : extensionList) {
			list.addAll(extension.print());
		}
		list.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void read(PrintInformation pair) {
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

	public void updateHolidays() throws IOException {
		for (EventPluginExtension<?> extension : extensionList) {
			if (extension instanceof HolidayPlugin) {
				((HolidayPlugin) extension).updateHolidays();
			}
		}
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
}
