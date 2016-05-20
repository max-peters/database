package database.plugin.event.birthday;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin(Storage storage, Backup backup) {
		super("birthday", storage, backup);
	}

	@Override public Birthday create(NamedNodeMap nodeMap) {
		return new Birthday(nodeMap.getNamedItem("name").getNodeValue(), LocalDate.parse(nodeMap.getNamedItem("date").getNodeValue(), DateTimeFormatter.ofPattern("dd.MM.uuuu")));
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		add(new Birthday(	Terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)"),
							LocalDate.parse(Terminal.request("date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"))));
	}
}