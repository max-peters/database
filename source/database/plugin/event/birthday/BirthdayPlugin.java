package database.plugin.event.birthday;

import java.util.Map;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin(Storage storage) {
		super("birthday", storage);
	}

	@Override public Birthday create(Map<String, String> map) {
		return new Birthday(map);
	}
}