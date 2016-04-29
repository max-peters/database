package database.plugin.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.event.appointment.AppointmentPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.day.DayPlugin;
import database.plugin.event.holiday.HolidayPlugin;
import database.plugin.settings.Settings;

public class EventPlugin extends Plugin {
	private ArrayList<EventPluginExtension<? extends Event>>	extensionList	= new ArrayList<EventPluginExtension<? extends Event>>();
	private EventOutputFormatter								formatter;

	public EventPlugin(DayPlugin dayPlugin, BirthdayPlugin birthdayPlugin, HolidayPlugin holidayPlugin, AppointmentPlugin appointmentPlugin, Settings settings) {
		super("event");
		extensionList.add(dayPlugin);
		extensionList.add(birthdayPlugin);
		extensionList.add(holidayPlugin);
		extensionList.add(appointmentPlugin);
		formatter = new EventOutputFormatter(settings);
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		EventPluginExtension<? extends Event> extension = chooseType();
		if (extension != null) {
			extension.createRequest();
			Terminal.update();
		}
	}

	@Override @Command(tag = "display") public void display() throws InterruptedException, BadLocationException {
		EventPluginExtension<? extends Event> extension = chooseType();
		if (extension != null) {
			extension.display();
			Terminal.update();
		}
	}

	public Iterable<Event> getIterable() {
		List<Event> list = new LinkedList<Event>();
		for (InstancePlugin<? extends Event> extension : extensionList) {
			if (extension.getDisplay()) {
				list.addAll((Collection<? extends Event>) extension.getIterable());
			}
		}
		return list;
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = formatter.getInitialOutput(getIterable());
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(getIdentity() + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void print(Document document, Element element) {
		Element entryElement = document.createElement("display");
		entryElement.setAttribute("boolean", String.valueOf(getDisplay()));
		element.appendChild(entryElement);
	}

	@Override public void read(String nodeName, NamedNodeMap nodeMap) {
		if (nodeName.equals("display")) {
			setDisplay(Boolean.valueOf(nodeMap.getNamedItem("boolean").getNodeValue()));
		}
	}

	@Command(tag = "show") public void show() throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String command = Terminal.request("show", getCommandTags(formatter.getClass()));
		for (Method method : formatter.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				Object output = method.invoke(formatter, getIterable());
				Terminal.getLineOfCharacters('-');
				Terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				Terminal.waitForInput();
			}
		}
	}

	@Command(tag = "store") public void store() throws BadLocationException, InterruptedException {
		EventPluginExtension<? extends Event> extension = chooseType();
		if (extension != null) {
			extension.store();
			Terminal.update();
		}
	}

	private EventPluginExtension<? extends Event> chooseType() throws InterruptedException, BadLocationException {
		ArrayList<String> strings = new ArrayList<String>();
		EventPluginExtension<? extends Event> toReturn = null;
		String pluginIdentity;
		int position;
		for (EventPluginExtension<? extends Event> extension : extensionList) {
			strings.add(extension.getIdentity());
		}
		strings.remove(2);
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (EventPluginExtension<? extends Event> extension : extensionList) {
				if (pluginIdentity.equals(extension.getIdentity())) {
					toReturn = extension;
				}
			}
		}
		return toReturn;
	}
}
