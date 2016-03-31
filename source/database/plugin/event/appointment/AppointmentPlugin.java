package database.plugin.event.appointment;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class AppointmentPlugin extends EventPluginExtension<Appointment> {
	public AppointmentPlugin(Storage storage) {
		super("appointment", storage);
	}

	@Override public Appointment create(Map<String, String> map) {
		return new Appointment(map);
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", ".*");
		map.put("attribute", ".*");
		map.put("date", null);
		request(map);
		createAndAdd(map);
		update();
	}

	@Override public void add(Appointment appointment) {
		if (!appointment.date.isPast()) {
			super.add(appointment);
		}
	}
}
