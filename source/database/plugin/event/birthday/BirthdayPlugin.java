package database.plugin.event.birthday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin() {
		super("birthday", Birthday.class);
	}

	@Override public void createRequest(Terminal terminal, BackupService backupService) throws InterruptedException, BadLocationException {
		String name = terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Birthday birthday = new Birthday(name, date);
		add(birthday);
		backupService.backupCreation(birthday, this);
	}
}