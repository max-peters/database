package database.plugin.event;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import database.main.PluginContainer;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.FormatterProvider;
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
import database.plugin.task.Task;
import database.plugin.task.TaskPlugin;

public class EventPlugin extends Plugin {
	public EventPlugin() {
		super("event");
	}

	@Command(tag = "cancel") public void cancel(ITerminal terminal, BackupService backupService, PluginContainer pluginContainer,
												FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		boolean displayTemp = display;
		int i = 0;
		int position;
		Event temp = null;
		MultiDayAppointmentPlugin multiDayAppointmentPlugin = (MultiDayAppointmentPlugin) pluginContainer.getPlugin("multidayappointment");
		AppointmentPlugin appointmentPlugin = (AppointmentPlugin) pluginContainer.getPlugin("appointment");
		WeeklyAppointmentPlugin weeklyAppointmentPlugin = (WeeklyAppointmentPlugin) pluginContainer.getPlugin("weeklyappointment");
		List<EventPluginExtension<? extends Event>> list = new LinkedList<EventPluginExtension<? extends Event>>();
		list.add(appointmentPlugin);
		list.add(weeklyAppointmentPlugin);
		list.add(multiDayAppointmentPlugin);
		Iterable<Event> iterable = getNearEvents(getIterable(list), ((Settings) pluginContainer.getPlugin("settings")).getDisplayedDays());
		List<String> stringList = formatOutput(iterable);
		display = false;
		terminal.update(pluginContainer, formatterProvider);
		position = terminal.checkRequest(stringList);
		if (position >= 0) {
			for (Event event : iterable) {
				if (i == position) {
					temp = event;
					break;
				}
				i++;
			}
			if (temp instanceof WeeklyAppointment) {
				backupService.backupChangeBefor(temp, weeklyAppointmentPlugin);
				temp.date = temp.date.plusDays(7);
				backupService.backupChangeAfter(temp, weeklyAppointmentPlugin);
			}
			else if (temp instanceof MultiDayAppointment) {
				multiDayAppointmentPlugin.remove(temp);
				backupService.backupRemoval(temp, multiDayAppointmentPlugin);
			}
			else if (temp instanceof Appointment) {
				appointmentPlugin.remove(temp);
				backupService.backupRemoval(temp, appointmentPlugin);
			}
		}
		display = displayTemp;
		terminal.update(pluginContainer, formatterProvider);
	}

	@Command(tag = "check") public void check(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider)	throws InterruptedException,
																																		BadLocationException {
		boolean displayTemp = display;
		List<EventPluginExtension<? extends Event>> list = new LinkedList<EventPluginExtension<? extends Event>>(getExtensionMap(pluginContainer).values());
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
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
		display = false;
		terminal.update(pluginContainer, formatterProvider);
		terminal.printLine("event", StringType.REQUEST, StringFormat.BOLD);
		terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
		for (String string : formatOutput(eventList)) {
			terminal.printLine(" ->" + string, StringType.SOLUTION, StringFormat.STANDARD);
		}
		terminal.waitForInput();
		display = displayTemp;
		terminal.update(pluginContainer, formatterProvider);
	}

	@Command(tag = "new") public void createRequest(ITerminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		List<String> list = new ArrayList<String>(getExtensionMap(pluginContainer).keySet());
		list.remove("holiday");
		EventPluginExtension<? extends Event> extension = chooseType(list, terminal, pluginContainer);
		if (extension != null) {
			extension.createRequest(terminal, backupService);
			terminal.update(pluginContainer, formatterProvider);
		}
	}

	@Override @Command(tag = "display") public void display(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider)	throws InterruptedException,
																																						BadLocationException {
		EventPluginExtension<? extends Event> extension = chooseType(new ArrayList<String>(getExtensionMap(pluginContainer).keySet()), terminal, pluginContainer);
		if (extension != null) {
			extension.display(terminal, pluginContainer, formatterProvider);
			terminal.update(pluginContainer, formatterProvider);
		}
	}

	public Iterable<Event> getIterable(Iterable<EventPluginExtension<? extends Event>> extensionList) {
		List<Event> list = new LinkedList<Event>();
		for (InstancePlugin<? extends Event> extension : extensionList) {
			if (extension.display) {
				for (Event event : extension.getIterable()) {
					int i = list.size();
					while (i > 0 && list.get(i - 1).compareTo(event) > 0) {
						i--;
					}
					list.add(i, event);
				}
			}
		}
		return list;
	}

	@Override public void initialOutput(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException {
		String output = "";
		for (String string : formatOutput(getNearEvents(addTaskDates(getIterable(getExtensionMap(pluginContainer).values()), (TaskPlugin) pluginContainer.getPlugin("task")),
														((Settings) pluginContainer.getPlugin("settings")).getDisplayedDays()))) {
			output += string + System.getProperty("line.separator");
		}
		if (!output.isEmpty()) {
			terminal.printLine(identity, StringType.MAIN, StringFormat.BOLD);
			terminal.printLine(output, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void print(Document document, Element appendTo) {
		Element entryElement = document.createElement("display");
		entryElement.setTextContent(String.valueOf(display));
		appendTo.appendChild(entryElement);
	}

	@Override public void read(Node node) {
		if (node.getNodeName().equals("display")) {
			display = Boolean.valueOf(node.getTextContent());
		}
	}

	@Command(tag = "show") public void show(ITerminal terminal, PluginContainer pluginContainer,
											FormatterProvider formatterProvider)	throws InterruptedException, BadLocationException, IllegalAccessException,
																					IllegalArgumentException, InvocationTargetException {
		boolean displayTemp = display;
		display = false;
		terminal.update(pluginContainer, formatterProvider);
		terminal.getLineOfCharacters('-', StringType.SOLUTION);
		terminal.printLine(	printAll(addTaskDates(getIterable(getExtensionMap(pluginContainer).values()), (TaskPlugin) pluginContainer.getPlugin("task"))), StringType.SOLUTION,
							StringFormat.STANDARD);
		terminal.waitForInput();
		display = displayTemp;
		terminal.update(pluginContainer, formatterProvider);
	}

	@Command(tag = "store") public void store(PluginContainer pluginContainer, ITerminal terminal, FormatterProvider formatterProvider)	throws BadLocationException,
																																		InterruptedException {
		List<String> list = new ArrayList<String>(getExtensionMap(pluginContainer).keySet());
		list.remove("holiday");
		EventPluginExtension<? extends Event> extension = chooseType(list, terminal, pluginContainer);
		if (extension != null) {
			extension.store(pluginContainer, terminal, formatterProvider);
			terminal.update(pluginContainer, formatterProvider);
		}
	}

	private Iterable<Event> addTaskDates(Iterable<Event> iterable, TaskPlugin taskPlugin) {
		List<Event> list = new LinkedList<Event>();
		for (Event event : iterable) {
			list.add(event);
		}
		for (Task task : taskPlugin.getIterable()) {
			if (task.date != null) {
				int i = list.size();
				Appointment appointment = new Appointment(task.name, task.date, null, null);
				while (i > 0 && list.get(i - 1).compareTo(appointment) > 0) {
					i--;
				}
				list.add(i, appointment);
			}
		}
		return list;
	}

	private EventPluginExtension<? extends Event> chooseType(List<String> strings, ITerminal terminal, PluginContainer pluginContainer)	throws InterruptedException,
																																		BadLocationException {
		EventPluginExtension<? extends Event> toReturn = null;
		int position = terminal.checkRequest(strings);
		if (position != -1) {
			toReturn = (EventPluginExtension<? extends Event>) pluginContainer.getPlugin(strings.get(position));
		}
		return toReturn;
	}

	private List<String> formatOutput(Iterable<? extends Event> iterable) {
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

	private Map<String, EventPluginExtension<? extends Event>> getExtensionMap(PluginContainer pluginContainer) {
		Map<String, EventPluginExtension<? extends Event>> extensionMap = new LinkedHashMap<String, EventPluginExtension<? extends Event>>();
		DayPlugin dayPlugin = (DayPlugin) pluginContainer.getPlugin("day");
		BirthdayPlugin birthdayPlugin = (BirthdayPlugin) pluginContainer.getPlugin("birthday");
		HolidayPlugin holidayPlugin = (HolidayPlugin) pluginContainer.getPlugin("holiday");
		AppointmentPlugin appointmentPlugin = (AppointmentPlugin) pluginContainer.getPlugin("appointment");
		WeeklyAppointmentPlugin weeklyAppointmentPlugin = (WeeklyAppointmentPlugin) pluginContainer.getPlugin("weeklyappointment");
		MultiDayAppointmentPlugin multiDayAppointmentPlugin = (MultiDayAppointmentPlugin) pluginContainer.getPlugin("multidayappointment");
		extensionMap.put(holidayPlugin.identity, holidayPlugin);
		extensionMap.put(dayPlugin.identity, dayPlugin);
		extensionMap.put(birthdayPlugin.identity, birthdayPlugin);
		extensionMap.put(appointmentPlugin.identity, appointmentPlugin);
		extensionMap.put(weeklyAppointmentPlugin.identity, weeklyAppointmentPlugin);
		extensionMap.put(multiDayAppointmentPlugin.identity, multiDayAppointmentPlugin);
		return extensionMap;
	}

	private ArrayList<Event> getNearEvents(Iterable<? extends Event> iterable, int displayRange) {
		ArrayList<Event> nearEvents = new ArrayList<Event>();
		for (Event event : iterable) {
			if (ChronoUnit.DAYS.between(LocalDate.now(), event.updateYear()) <= displayRange) {
				nearEvents.add(event);
			}
		}
		return nearEvents;
	}

	private String printAll(Iterable<Event> iterable) throws BadLocationException {
		String output = "";
		for (String string : formatOutput(iterable)) {
			output += string + System.getProperty("line.separator");
		}
		return output;
	}
}
