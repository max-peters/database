package database.plugin.event.day;

import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.plugin.Backup;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class DayPlugin extends EventPluginExtension<Day> {
	public DayPlugin(Storage storage, Backup backup) {
		super("day", storage, backup);
	}

	@Override public Day create(Map<String, String> parameter) {
		return new Day(parameter.get("name"), new Date(parameter.get("date")));
	}

	@Override public Day create(NamedNodeMap nodeMap) {
		return new Day(nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()));
	}
}