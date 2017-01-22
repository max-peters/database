package database.plugin.plugin;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.connector.AppointmentDatabaseConnector;
import database.plugin.connector.HolidayDatabaseConnector;
import database.plugin.connector.HolidayURLConnector;
import database.plugin.element.Appointment;
import database.plugin.element.Birthday;
import database.plugin.element.CalendarElement;
import database.plugin.element.Day;
import database.plugin.element.Holiday;
import database.plugin.outputHandler.CalendarOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;
import database.services.settings.Settings;
import database.services.stringComplete.IFrequentStringComplete;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.ChangeCommand;
import database.services.undoRedo.command.DeleteCommand;
import database.services.undoRedo.command.InsertCommand;
import database.services.undoRedo.command.UndoableCommand;

public class CalendarPlugin extends Plugin {
	public CalendarPlugin() throws SQLException, BadLocationException, InterruptedException {
		super("event", new CalendarOutputHandler());
		ServiceRegistry.Instance().get(IFrequentStringComplete.class).create("appointment");
	}

	@Command(tag = "cancel") public void cancel() throws BadLocationException, InterruptedException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		Settings settings = ServiceRegistry.Instance().get(Settings.class);
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		AppointmentDatabaseConnector appointmentConnector = (AppointmentDatabaseConnector) registry.get(Appointment.class);
		boolean displayTemp = display;
		int position;
		Appointment appointment = null;
		UndoableCommand command = null;
		List<CalendarElement> list = new LinkedList<>();
		list.addAll(appointmentConnector.getList());
		list.removeIf(c -> !c.isNear(settings.eventDisplayRange));
		Collections.sort(list);
		List<String> stringList = getOutputHandler().formatOutput(list).toStringList();
		display = false;
		terminal.update();
		position = terminal.checkRequest(stringList, "choose event to cancel");
		if (position >= 0) {
			appointment = (Appointment) list.get(position);
			if (appointment.isRepeatable()) {
				position = terminal.checkRequest(Arrays.asList("this appointment", "this and all following appointments"), "cancel");
				switch (position) {
					case 0:
						command = new ChangeCommand(appointment, appointment.repeat());
						break;
					case 1:
						command = new DeleteCommand(appointment);
						break;
				}
			}
			else {
				command = new DeleteCommand(appointment);
			}
		}
		display = displayTemp;
		terminal.update();
		CommandHandler.Instance().executeCommand(command);
	}

	@Command(tag = "check") public void check() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		boolean displayTemp = display;
		String temp = terminal.request("date", RequestType.DATE);
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		List<CalendarElement> list = new LinkedList<>();
		String output = null;
		for (CalendarElement element : getOutputHandler().getSortedIterable(-1)) {
			if (element.updateYear().isEqual(date)) {
				list.add(element);
			}
		}
		if (list.size() == 0) {
			output = " there are no entries for this date";
		}
		else if (list.size() == 1) {
			output = " there is an entry for this date:";
		}
		else if (list.size() == 2) {
			output = " there are entries for this date:";
		}
		display = false;
		terminal.update();
		terminal.printLine("event", StringType.REQUEST, StringFormat.BOLD);
		terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
		for (String string : getOutputHandler().formatOutput(list).toStringList()) {
			terminal.printLine(" ->" + string, StringType.SOLUTION, StringFormat.STANDARD);
		}
		terminal.waitForInput();
		display = displayTemp;
		terminal.update();
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		String name;
		String temp = "";
		LocalDate date;
		CalendarElement element = null;
		List<String> types = Arrays.asList("appointment", "birthday", "day");
		int position = terminal.checkRequest(types, "choose event type");
		if (position == -1) {
			return;
		}
		switch (types.get(position)) {
			case "appointment":
				IFrequentStringComplete frequentStringComplement = ServiceRegistry.Instance().get(IFrequentStringComplete.class);
				LocalTime begin;
				LocalTime end;
				LocalDate lastDay;
				int daysTilRepetition;
				temp = terminal.request("name", RequestType.NAME, frequentStringComplement.get("appointment"));
				name = frequentStringComplement.get("appointment").getCorrespondingString(temp);
				temp = terminal.request("date", RequestType.DATE);
				date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				temp = terminal.request("begin", RequestType.TIME);
				begin = temp.isEmpty() ? null : LocalTime.parse(temp, timeFormatter);
				temp = terminal.request("until", RequestType.DATE, date.format(dateFormatter));
				lastDay = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				while (lastDay.isBefore(date)) {
					terminal.errorMessage();
					temp = terminal.request("until", RequestType.DATE, date.format(dateFormatter));
					lastDay = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				}
				temp = terminal.request("end", RequestType.TIME);
				end = temp.isEmpty() ? null : LocalTime.parse(temp, timeFormatter);
				while (end != null && lastDay.isEqual(date) && !end.isAfter(begin)) {
					terminal.errorMessage();
					temp = terminal.request("end", RequestType.TIME);
					end = temp.isEmpty() ? null : LocalTime.parse(temp, timeFormatter);
				}
				daysTilRepetition = Integer.valueOf(terminal.request("days til repetition", RequestType.INTEGER, "0"));
				element = new Appointment(name, date, begin, lastDay, end, daysTilRepetition);
				frequentStringComplement.insert(name, "appointment");
				break;
			case "birthday":
				name = terminal.request("name", RequestType.NAME);
				temp = terminal.request("date", RequestType.DATE);
				date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				element = new Birthday(name, date);
				break;
			case "day":
				name = terminal.request("name", RequestType.NAME_NUMBER);
				temp = terminal.request("date", RequestType.DATE);
				date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				element = new Day(name, date);
				break;
		}
		CommandHandler.Instance().executeCommand(new InsertCommand(element));
	}

	@Override public CalendarOutputHandler getOutputHandler() {
		return (CalendarOutputHandler) super.getOutputHandler();
	}

	@Override @Command(tag = "show") public void show()	throws SQLException, BadLocationException, InterruptedException, IllegalAccessException, IllegalArgumentException,
														InvocationTargetException, UserCancelException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		boolean displayTemp = display;
		display = false;
		terminal.update();
		super.show();
		display = displayTemp;
		terminal.update();
	}

	public void updateCalendar() throws SQLException, BadLocationException, InterruptedException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		AppointmentDatabaseConnector appointmentConnector = (AppointmentDatabaseConnector) registry.get(Appointment.class);
		HolidayDatabaseConnector holidayConnector = (HolidayDatabaseConnector) registry.get(Holiday.class);
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		HolidayURLConnector connector = new HolidayURLConnector();
		List<Holiday> holidayList = holidayConnector.getList();
		List<Appointment> appointmentList = appointmentConnector.getList();
		if (holidayList.isEmpty()) {
			connector.getHolidays();
		}
		else {
			for (Holiday holiday : holidayList) {
				if (holiday.isPast()) {
					connector.getHolidays();
					break;
				}
			}
		}
		for (Appointment appointment : appointmentList) {
			if (appointment.isPast()) {
				database.remove(appointment);
				if (appointment.isRepeatable()) {
					Appointment temp;
					do {
						temp = appointment.repeat();
					}
					while (temp.isPast());
					database.insert(temp);
				}
			}
		}
	}
}
