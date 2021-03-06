package database.plugin.calendar;

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
import database.main.userInterface.OutputType;
import database.main.userInterface.RequestType;
import database.main.userInterface.StringFormat;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.calendar.connector.AppointmentDatabaseConnector;
import database.plugin.calendar.connector.HolidayDatabaseConnector;
import database.plugin.calendar.connector.HolidayURLConnector;
import database.plugin.calendar.element.Appointment;
import database.plugin.calendar.element.Birthday;
import database.plugin.calendar.element.CalendarElement;
import database.plugin.calendar.element.Day;
import database.plugin.calendar.element.Holiday;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;
import database.services.settings.ISettingsProvider;
import database.services.settings.InternalParameters;
import database.services.stringComplete.IFrequentStringComplete;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.ChangeCommand;
import database.services.undoRedo.command.DeleteCommand;
import database.services.undoRedo.command.InsertCommand;
import database.services.undoRedo.command.UndoableCommand;

public class CalendarPlugin extends Plugin {
	public CalendarPlugin() throws SQLException, BadLocationException, InterruptedException {
		super("event", new CalendarOutputHandler());
	}

	@Command(tag = "cancel")
	public void cancel() throws BadLocationException, InterruptedException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		InternalParameters internalParameters = ServiceRegistry.Instance().get(ISettingsProvider.class)
				.getInternalParameters();
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		AppointmentDatabaseConnector appointmentConnector = (AppointmentDatabaseConnector) registry
				.get(Appointment.class);
		boolean displayTemp = display;
		int position;
		Appointment appointment = null;
		UndoableCommand command = null;
		List<CalendarElement> list = new LinkedList<>();
		list.addAll(appointmentConnector.getList());
		list.removeIf(c -> !c.isNear(internalParameters.getEventDisplayRange()));
		Collections.sort(list);
		List<String> stringList = getOutputHandler().formatOutput(list, false).toStringList();
		display = false;
		terminal.update();
		position = terminal.checkRequest(stringList, "choose event to cancel");
		if (position >= 0) {
			appointment = (Appointment) list.get(position);
			if (appointment.isRepeatable()) {
				position = terminal.checkRequest(
						Arrays.asList("this appointment", "this and all following appointments"), "cancel");
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

	@Command(tag = "check")
	public void check() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		boolean displayTemp = display;
		String temp = terminal.request("enter date", RequestType.DATE);
		LocalDate date = temp.isEmpty() ? LocalDate.now()
				: LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
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
		terminal.printLine(output, OutputType.CLEAR, StringFormat.STANDARD);
		for (String string : getOutputHandler().formatOutput(list, false).toStringList()) {
			terminal.printLine(" ->" + string, OutputType.ADD, StringFormat.STANDARD);
		}
		terminal.waitForInput();
		display = displayTemp;
		terminal.update();
	}

	@Command(tag = "new")
	public void createRequest() throws BadLocationException, InterruptedException, UserCancelException, SQLException {
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
				IFrequentStringComplete frequentStringComplement = ServiceRegistry.Instance()
						.get(IFrequentStringComplete.class);
				LocalTime begin;
				LocalTime end = LocalTime.MIN;
				LocalDate lastDay;
				int daysTilRepetition;
				String spezification;
				temp = terminal.request("enter appointment name", RequestType.NAME,
						frequentStringComplement.get("appointment"));
				name = frequentStringComplement.get("appointment").getCorrespondingString(temp);
				temp = terminal.request("enter date", RequestType.DATE);
				date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				temp = terminal.request("enter begin time", RequestType.TIME);
				begin = temp.isEmpty() ? LocalTime.MIN : LocalTime.parse(temp, timeFormatter);
				temp = terminal.request("until which date", RequestType.DATE, date.format(dateFormatter));
				lastDay = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				while (lastDay.isBefore(date)) {
					terminal.errorMessage();
					temp = terminal.request("until which date", RequestType.DATE, date.format(dateFormatter));
					lastDay = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				}
				temp = terminal.request("enter end time", RequestType.TIME);
				end = temp.isEmpty() ? LocalTime.MIN : LocalTime.parse(temp, timeFormatter);
				while (!end.equals(LocalTime.MIN) && lastDay.isEqual(date) && !end.isAfter(begin)) {
					terminal.errorMessage();
					temp = terminal.request("enter end time", RequestType.TIME);
					end = temp.isEmpty() ? LocalTime.MIN : LocalTime.parse(temp, timeFormatter);
				}
				daysTilRepetition = Integer
						.valueOf(terminal.request("enter days til repetition", RequestType.INTEGER, "0"));
				spezification = terminal.request("enter spezification", RequestType.NAME_NUMBER_EMPTY);
				element = new Appointment(name, date, begin, lastDay, end, daysTilRepetition, spezification);
				frequentStringComplement.insert(name, "appointment");
				break;
			case "birthday":
				name = terminal.request("enter name", RequestType.NAME);
				temp = terminal.request("enter date of birth", RequestType.DATE);
				date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				element = new Birthday(name, date);
				break;
			case "day":
				name = terminal.request("enter day description", RequestType.NAME_NUMBER);
				temp = terminal.request("enter date", RequestType.DATE);
				date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, dateFormatter);
				element = new Day(name, date);
				break;
		}
		CommandHandler.Instance().executeCommand(new InsertCommand(element));
	}

	@Override
	public CalendarOutputHandler getOutputHandler() {
		return (CalendarOutputHandler) super.getOutputHandler();
	}

	@Override
	@Command(tag = "show")
	public void show() throws SQLException, BadLocationException, InterruptedException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, UserCancelException {
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
		AppointmentDatabaseConnector appointmentConnector = (AppointmentDatabaseConnector) registry
				.get(Appointment.class);
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
