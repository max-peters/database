package database.plugin.event.day;

import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.userInterface.Terminal;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage) {
		super("day", storage);
	}

	@Override public Day create(NamedNodeMap nodeMap) {
		return new Day(nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()));
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		add(new Day(Terminal.request("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)"), new Date(Terminal.request("date", "DATE"))));
		Terminal.update();
	}
}