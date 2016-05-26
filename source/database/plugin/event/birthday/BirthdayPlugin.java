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
		Birthday birthday = new Birthday(	Terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)"),
											LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu")));
		add(birthday);
		BackupService.backupCreation(birthday, this);
	}
}