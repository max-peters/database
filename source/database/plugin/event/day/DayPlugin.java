package database.plugin.event.day;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin() {
		super("day", Day.class);
	}

	@Override public void createRequest(ITerminal terminal, BackupService backupService)	throws InterruptedException, BadLocationException, UserCancelException,
																							SQLException {
		String name = terminal.request("name", ".+");
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Day day = new Day(name, date);
		add(day);
		backupService.backupCreation(day, this);
	}
}