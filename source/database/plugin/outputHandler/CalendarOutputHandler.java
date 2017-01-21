package database.plugin.outputHandler;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import database.plugin.Command;
import database.plugin.IOutputHandler;
import database.plugin.connector.AppointmentDatabaseConnector;
import database.plugin.connector.BirthdayDatabaseConnector;
import database.plugin.connector.DayDatabaseConnector;
import database.plugin.connector.HolidayDatabaseConnector;
import database.plugin.element.Appointment;
import database.plugin.element.Birthday;
import database.plugin.element.CalendarElement;
import database.plugin.element.Day;
import database.plugin.element.Holiday;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.settings.Settings;
import database.services.stringUtility.Builder;
import database.services.stringUtility.StringUtility;

public class CalendarOutputHandler implements IOutputHandler {
	public Builder formatOutput(Iterable<? extends CalendarElement> iterable) {
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		int longestNameLength = 0;
		int gap = 3;
		for (CalendarElement element : iterable) {
			if (element.getName().length() > longestNameLength) {
				longestNameLength = element.getName().length();
			}
		}
		for (CalendarElement element : iterable) {
			if (element.updateYear().isEqual(LocalDate.now())) {
				builder.append(" TODAY     ");
			}
			else {
				builder.append(" " + element.updateYear().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
			}
			builder.append(" - " + stringUtility.postIncrementTo(element.getName(), longestNameLength + gap, ' '));
			builder.append(element.getAdditionToOutput());
			builder.newLine();
		}
		return builder;
	}

	@Override public String getInitialOutput() throws SQLException {
		Settings settings = ServiceRegistry.Instance().get(Settings.class);
		return formatOutput(getSortedIterable(settings.eventDisplayRange)).build();
	}

	public Iterable<CalendarElement> getSortedIterable(int days) throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		AppointmentDatabaseConnector appointmentConnector = (AppointmentDatabaseConnector) registry.get(Appointment.class);
		BirthdayDatabaseConnector birthdayConnector = (BirthdayDatabaseConnector) registry.get(Birthday.class);
		DayDatabaseConnector dayConnector = (DayDatabaseConnector) registry.get(Day.class);
		HolidayDatabaseConnector holidayConnector = (HolidayDatabaseConnector) registry.get(Holiday.class);
		List<CalendarElement> list = new LinkedList<>();
		list.addAll(appointmentConnector.getList());
		list.addAll(birthdayConnector.getList());
		list.addAll(dayConnector.getList());
		list.addAll(holidayConnector.getList());
		if (days != -1) {
			list.removeIf(c -> !c.isNear(days));
		}
		Collections.sort(list);
		return list;
	}

	@Command(tag = "all") public String outputAll() throws SQLException {
		return formatOutput(getSortedIterable(365)).build();
	}
}
