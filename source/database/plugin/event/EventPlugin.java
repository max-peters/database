package database.plugin.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.event.appointment.Appointment;
import database.plugin.event.appointment.AppointmentPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.day.DayPlugin;
import database.plugin.event.holiday.HolidayPlugin;
import database.plugin.event.weeklyAppointment.WeeklyAppointment;
import database.plugin.event.weeklyAppointment.WeeklyAppointmentPlugin;
import database.plugin.settings.Settings;

public class EventPlugin extends Plugin {
	private Map<String, EventPluginExtension<? extends Event>>	extensionMap	= new HashMap<String, EventPluginExtension<? extends Event>>();
	private EventOutputFormatter								formatter;

	public EventPlugin(	DayPlugin dayPlugin, BirthdayPlugin birthdayPlugin, HolidayPlugin holidayPlugin, AppointmentPlugin appointmentPlugin,
						WeeklyAppointmentPlugin weeklyAppointmentPlugin, Settings settings, Backup backup) {
		super("event", backup);
		extensionMap.put(dayPlugin.getIdentity(), dayPlugin);
		extensionMap.put(birthdayPlugin.getIdentity(), birthdayPlugin);
		extensionMap.put(holidayPlugin.getIdentity(), holidayPlugin);
		extensionMap.put(appointmentPlugin.getIdentity(), appointmentPlugin);
		extensionMap.put(weeklyAppointmentPlugin.getIdentity(), weeklyAppointmentPlugin);
		formatter = new EventOutputFormatter(settings);
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		List<String> list = new ArrayList<String>(extensionMap.keySet());
		list.remove("holiday");
		EventPluginExtension<? extends Event> extension = chooseType(list);
		if (extension != null) {
			backup.backup();
			extension.createRequest();
			Terminal.update();
		}
	}

	@Override @Command(tag = "display") public void display() throws InterruptedException, BadLocationException {
		EventPluginExtension<? extends Event> extension = chooseType(new ArrayList<String>(extensionMap.keySet()));
		if (extension != null) {
			extension.display();
			Terminal.update();
		}
	}

	public Iterable<Event> getIterable(Iterable<EventPluginExtension<? extends Event>> extensionList) {
		List<Event> list = new LinkedList<Event>();
		for (InstancePlugin<? extends Event> extension : extensionList) {
			if (extension.getDisplay()) {
				for (Event event : extension.getIterable()) {
					int i = list.size();
					while (i > 0 && list.get(i - 1).updateYear().compareTo(event.updateYear()) > 0) {
						i--;
					}
					list.add(i, event);
				}
			}
		}
		return list;
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = formatter.getInitialOutput(getIterable(extensionMap.values()));
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
				Object output = method.invoke(formatter, getIterable(extensionMap.values()));
				Terminal.getLineOfCharacters('-');
				Terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				Terminal.waitForInput();
			}
		}
	}

	@Command(tag = "store") public void store() throws BadLocationException, InterruptedException {
		List<String> list = new ArrayList<String>(extensionMap.keySet());
		list.remove("holiday");
		EventPluginExtension<? extends Event> extension = chooseType(list);
		if (extension != null) {
			extension.store();
			Terminal.update();
		}
	}

	@Command(tag = "cancel") public void cancel() throws BadLocationException, InterruptedException {
		boolean display = getDisplay();
		int i = 0;
		int position;
		Event temp = null;
		List<EventPluginExtension<? extends Event>> list = new LinkedList<EventPluginExtension<? extends Event>>();
		list.add(extensionMap.get("appointment"));
		list.add(extensionMap.get("weeklyappointment"));
		Iterable<Event> iterable = formatter.getNearEvents(getIterable(list));
		List<String> stringList = formatter.formatOutput(iterable);
		setDisplay(false);
		Terminal.update();
		position = Terminal.checkRequest(stringList);
		if (position >= 0) {
			for (Event event : iterable) {
				if (i == position) {
					temp = event;
					break;
				}
				i++;
			}
			if (temp instanceof WeeklyAppointment) {
				temp.date.addDays(7);
			}
			else if (temp instanceof Appointment) {
				extensionMap.get("appointment").remove(temp);
			}
			setDisplay(display);
			Terminal.update();
		}
	}

	private EventPluginExtension<? extends Event> chooseType(List<String> strings) throws InterruptedException, BadLocationException {
		EventPluginExtension<? extends Event> toReturn = null;
		int position = Terminal.checkRequest(strings);
		if (position != -1) {
			toReturn = extensionMap.get(strings.get(position));
		}
		return toReturn;
	}
}
