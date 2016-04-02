package database.plugin.event.birthday;

import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.plugin.Backup;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin(Storage storage, Backup backup) {
		super("birthday", storage, backup);
	}

	@Override public Birthday create(Map<String, String> parameter) {
		return new Birthday(parameter.get("name"), new Date(parameter.get("date")));
	}

	@Override public Birthday create(NamedNodeMap nodeMap) {
		return new Birthday(nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()));
	}
}