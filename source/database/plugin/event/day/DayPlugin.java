package database.plugin.event.day;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage, Backup backup) {
		super("day", storage, backup, Day.class);
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		add(new Day(Terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)"),
					LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"))));
	}
}