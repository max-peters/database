package database.plugin.event.birthday;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPluginExtension;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin() {
		super("birthday", Birthday.class);
	}

	@Override public void createRequest(ITerminal terminal, BackupService backupService)	throws InterruptedException, BadLocationException, UserCancelException,
																							SQLException {
		String name = terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Birthday birthday = new Birthday(name, date);
		add(birthday);
		backupService.backupCreation(birthday, this);
	}
}