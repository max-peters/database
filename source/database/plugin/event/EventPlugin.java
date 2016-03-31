package database.plugin.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
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

	@Override public Event create(Map<String, String> parameter) {
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

	@Override public void print(Document document, Element element) {
		for (EventPluginExtension<?> extension : extensionList) {
			extension.print(document, element);
		}
		Element entryElement = document.createElement("display");
		entryElement.setAttribute("boolean", String.valueOf(getDisplay()));
		element.appendChild(entryElement);
	}

	@Override public void read(String nodeName, Map<String, String> parameter) {
		if (nodeName.equals("display")) {
			setDisplay(Boolean.valueOf(parameter.get("boolean")));
		}
		else {
			for (EventPluginExtension<?> extension : extensionList) {
				if (nodeName.equals(extension.getIdentity())) {
					extension.createAndAdd(parameter);
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
