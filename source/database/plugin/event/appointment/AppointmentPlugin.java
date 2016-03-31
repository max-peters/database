package database.plugin.event.appointment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class AppointmentPlugin extends EventPluginExtension<Appointment> {
	public AppointmentPlugin(Storage storage) {
		super("appointment", new AppointmentList(), storage);
	}

	@Override public Appointment create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
																	InvocationTargetException, NoSuchMethodException, SecurityException {
		return new Appointment(map);
	}

	@Override public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
											InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", ".*");
		map.put("attribute", ".*");
		map.put("date", null);
		request(map);
		createAndAdd(map);
		update();
	}
}
