package database.plugin.event.birthday;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class BirthdayPlugin extends EventPluginExtension<Birthday> {
	public BirthdayPlugin(Storage storage) {
		super("birthday", new BirthdayList(), storage);
	}

	@Override public Birthday create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
																InvocationTargetException, NoSuchMethodException, SecurityException {
		return new Birthday(map);
	}
}