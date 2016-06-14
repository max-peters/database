package database.plugin.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.backup.BackupService;
import database.plugin.event.appointment.Appointment;
import database.plugin.event.appointment.AppointmentPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.day.DayPlugin;
import database.plugin.event.holiday.HolidayPlugin;
import database.plugin.event.multiDayAppointment.MultiDayAppointment;
import database.plugin.event.multiDayAppointment.MultiDayAppointmentPlugin;
import database.plugin.event.weeklyAppointment.WeeklyAppointment;
import database.plugin.event.weeklyAppointment.WeeklyAppointmentPlugin;
import database.plugin.settings.Settings;

public class EventPlugin extends Plugin {
	private Map<String, EventPluginExtension<? extends Event>>	extensionMap	= new LinkedHashMap<String, EventPluginExtension<? extends Event>>();
	private EventOutputFormatter								formatter;

	public EventPlugin(	DayPlugin dayPlugin, BirthdayPlugin birthdayPlugin, HolidayPlugin holidayPlugin, AppointmentPlugin appointmentPlugin,
						WeeklyAppointmentPlugin weeklyAppointmentPlugin, MultiDayAppointmentPlugin multiDayAppointmentPlugin, Settings settings) {
		super("event");
		extensionMap.put(holidayPlugin.getIdentity(), holidayPlugin);
		extensionMap.put(dayPlugin.getIdentity(), dayPlugin);
		extensionMap.put(birthdayPlugin.getIdentity(), birthdayPlugin);
		extensionMap.put(appointmentPlugin.getIdentity(), appointmentPlugin);
		extensionMap.put(weeklyAppointmentPlugin.getIdentity(), weeklyAppointmentPlugin);
		extensionMap.put(multiDayAppointmentPlugin.getIdentity(), multiDayAppointmentPlugin);
		formatter = new EventOutputFormatter(settings);
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		List<String> list = new ArrayList<String>(extensionMap.keySet());
		list.remove("holiday");
		EventPluginExtension<? extends Event> extension = chooseType(list);
		if (extension != null) {
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

	@Command(tag = "check") public void check() throws InterruptedException, BadLocationException {
		boolean display = getDisplay();
		List<EventPluginExtension<? extends Event>> list = new LinkedList<EventPluginExtension<? extends Event>>(extensionMap.values());
		LocalDate date = LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		List<Event> eventList = new LinkedList<Event>();
		String output = null;
		for (EventPluginExtension<? extends Event> plugin : list) {
			eventList.addAll(plugin.getEvents(date));
		}
		if (eventList.size() == 0) {
			output = " there are no appointments for this date";
		}
		else if (eventList.size() == 1) {
			output = " there is an appointment for this date:";
		}
		else if (eventList.size() == 2) {
			output = " there are appointments for this date:";
		}
		setDisplay(false);
		Terminal.update();
		Terminal.printLine("event", StringType.REQUEST, StringFormat.BOLD);
		Terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
		for (String string : formatter.formatOutput(eventList)) {
			Terminal.printLine(" ->" + string, StringType.SOLUTION, StringFormat.STANDARD);
		}
		Terminal.waitForInput();
		setDisplay(display);
		Terminal.update();
	}

	public Iterable<Event> getIterable(Iterable<EventPluginExtension<? extends Event>> extensionList) {
		List<Event> list = new LinkedList<Event>();
		for (InstancePlugin<? extends Event> extension : extensionList) {
			if (extension.getDisplay()) {
				for (Event event : extension.getIterable()) {
					int i = list.size();
					while (i > 0 && new Comparator<Event>() {
						@Override public int compare(Event first, Event second) {
							LocalDateTime e1;
							LocalDateTime e2;
							if (first instanceof Appointment) {
								if (((Appointment) first).begin != null) {
									e1 = first.date.atTime(((Appointment) first).begin);
								}
								else {
									e1 = first.updateYear().atTime(0, 0);
								}
							}
							else {
								e1 = first.updateYear().atTime(23, 59);
							}
							if (second instanceof Appointment) {
								if (((Appointment) second).begin != null) {
									e2 = second.date.atTime(((Appointment) second).begin);
								}
								else {
									e2 = second.updateYear().atTime(0, 0);
								}
							}
							else {
								e2 = second.updateYear().atTime(23, 59);
							}
							return e1.compareTo(e2);
						}
					}.compare(list.get(i - 1), event) > 0) {
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
			Terminal.printLine(getIdentity(), StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void print(Document document, Element appendTo) {
		Element entryElement = document.createElement("display");
		entryElement.setTextContent(String.valueOf(getDisplay()));
		appendTo.appendChild(entryElement);
	}

	@Override public void read(Node node) {
		if (node.getNodeName().equals("display")) {
			setDisplay(Boolean.valueOf(node.getTextContent()));
		}
	}

	@Command(tag = "show") public void show() throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String command = Terminal.request("show", getCommandTags(formatter.getClass()));
		boolean display = false;
		for (Method method : formatter.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				if (command.equals("all")) {
					display = getDisplay();
					setDisplay(false);
					Terminal.update();
				}
				Object output = method.invoke(formatter, getIterable(extensionMap.values()));
				Terminal.getLineOfCharacters('-', StringType.SOLUTION);
				Terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				Terminal.waitForInput();
				if (command.equals("all")) {
					setDisplay(display);
					Terminal.update();
				}
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
		list.add(extensionMap.get("multidayappointment"));
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
				temp.date = temp.date.plusDays(7);
			}
			else if (temp instanceof MultiDayAppointment) {
				extensionMap.get("multidayappointment").remove(temp);
				BackupService.backupRemoval(temp, extensionMap.get("multidayappointment"));
			}
			else if (temp instanceof Appointment) {
				extensionMap.get("appointment").remove(temp);
				BackupService.backupRemoval(temp, extensionMap.get("appointment"));
			}
		}
		setDisplay(display);
		Terminal.update();
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
