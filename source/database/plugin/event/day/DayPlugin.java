package database.plugin.event.day;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Storage;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage) {
		super("day", storage, Day.class);
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		String name = Terminal.request("name", ".+");
		String temp = Terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Day day = new Day(name, date);
		add(day);
		BackupService.backupCreation(day, this);
	}
}