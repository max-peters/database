package database.plugin.event.birthday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Storage;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin(Storage storage) {
		super("birthday", storage, Birthday.class);
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		String name = Terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		String temp = Terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Birthday birthday = new Birthday(name, date);
		add(birthday);
		BackupService.backupCreation(birthday, this);
	}
}