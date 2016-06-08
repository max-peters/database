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
		Day day = new Day(Terminal.request("name", ".+"), LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu")));
		add(day);
		BackupService.backupCreation(day, this);
	}
}