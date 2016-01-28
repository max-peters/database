package database.plugin.event.appointment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class AppointmentPlugin extends EventPluginExtention {
	public AppointmentPlugin(Storage storage) {
		super("appointment", new AppointmentList(), storage);
	}

	@Override public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
											InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", ".*");
		map.put("attribute", ".*");
		map.put("date", null);
		request(map);
		getInstanceList().add(map);
		update();
	}
}
