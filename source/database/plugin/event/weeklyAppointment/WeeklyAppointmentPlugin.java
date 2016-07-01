package database.plugin.event.weeklyAppointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class WeeklyAppointmentPlugin extends EventPluginExtension<WeeklyAppointment> {
	public WeeklyAppointmentPlugin() {
		super("weeklyappointment", WeeklyAppointment.class);
	}

	@Override public void add(WeeklyAppointment weeklyAppointment) {
		while (weeklyAppointment.date.isBefore(LocalDate.now()) && !weeklyAppointment.date.isEqual(LocalDate.now())
				|| weeklyAppointment.date.isEqual(LocalDate.now()) && weeklyAppointment.begin == null
				|| weeklyAppointment.date.isEqual(LocalDate.now())&& weeklyAppointment.begin != null && weeklyAppointment.end == null
					&& !weeklyAppointment.begin.isAfter(LocalTime.now())
				|| weeklyAppointment.date.isEqual(LocalDate.now())&& weeklyAppointment.begin != null && weeklyAppointment.end != null
					&& !weeklyAppointment.end.isAfter(LocalTime.now())) {
			weeklyAppointment.date = weeklyAppointment.date.plusDays(7);
		}
		super.add(weeklyAppointment);
	}

	@Override public void createRequest(Terminal terminal, BackupService backupService) throws InterruptedException, BadLocationException {
		String name;
		String temp = "";
		LocalTime begin;
		LocalDate date;
		name = terminal.request("name", ".+");
		temp = terminal.request("date", "DATE");
		date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		temp = terminal.request("begin", "TIME");
		begin = temp.isEmpty() ? null : LocalTime.parse(temp, DateTimeFormatter.ofPattern("HH:mm"));
		if (begin != null) {
			temp = terminal.request("end", "TIME");
			while (!temp.isEmpty() && begin.isBefore(LocalTime.parse(temp, DateTimeFormatter.ofPattern("HH:mm")))) {
				terminal.errorMessage();
				temp = terminal.request("end", "TIME");
			}
		}
		WeeklyAppointment weeklyAppointment = new WeeklyAppointment(name, date, begin, temp.isEmpty() ? null : LocalTime.parse(temp, DateTimeFormatter.ofPattern("HH:mm")));
		add(weeklyAppointment);
		backupService.backupCreation(weeklyAppointment, this);
	}
}
