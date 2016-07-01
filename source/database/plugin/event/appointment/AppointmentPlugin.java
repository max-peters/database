package database.plugin.event.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class AppointmentPlugin extends EventPluginExtension<Appointment> {
	public AppointmentPlugin() {
		super("appointment", Appointment.class);
	}

	@Override public void add(Appointment appointment) {
		if (!appointment.date.isBefore(LocalDate.now()) && !appointment.date.isEqual(LocalDate.now())|| appointment.date.isEqual(LocalDate.now()) && appointment.begin == null
			|| appointment.date.isEqual(LocalDate.now()) && appointment.begin != null && appointment.end == null && !appointment.begin.isBefore(LocalTime.now())
			|| appointment.date.isEqual(LocalDate.now()) && appointment.begin != null && appointment.end != null && !appointment.end.isBefore(LocalTime.now())) {
			int i = list.size();
			while (i > 0 && list.get(i - 1).date.isAfter(appointment.date)
					|| i > 0&& list.get(i - 1).date.isEqual(appointment.date)
						&& (list.get(i - 1).begin != null	? list.get(i - 1).begin
															: LocalTime.parse(	"00:00",
																				DateTimeFormatter.ofPattern("HH:mm"))).isAfter(appointment.begin != null	? appointment.begin
																																							: LocalTime.parse(	"00:00",
																																												DateTimeFormatter.ofPattern("HH:mm")))) {
				i--;
			}
			list.add(i, appointment);
		}
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
			while (!temp.isEmpty() && !begin.isBefore(LocalTime.parse(temp, DateTimeFormatter.ofPattern("HH:mm")))) {
				terminal.errorMessage();
				temp = terminal.request("end", "TIME");
			}
		}
		Appointment appointment = new Appointment(name, date, begin, temp.isEmpty() ? null : LocalTime.parse(temp, DateTimeFormatter.ofPattern("HH:mm")));
		add(appointment);
		backupService.backupCreation(appointment, this);
	}
}
