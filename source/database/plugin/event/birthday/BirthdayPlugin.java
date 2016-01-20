package database.plugin.event.birthday;

import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class BirthdayPlugin extends EventPluginExtention {
	public BirthdayPlugin(Storage storage) {
		super("birthday", new BirthdayList(), storage);
	}
}