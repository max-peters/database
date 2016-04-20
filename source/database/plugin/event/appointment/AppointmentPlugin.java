package database.plugin.event.appointment;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.plugin.Backup;
import database.plugin.event.EventPluginExtension;
import database.plugin.storage.Storage;

public class AppointmentPlugin extends EventPluginExtension<Appointment> {
	public AppointmentPlugin(Storage storage, Backup backup) {
		super("appointment", storage, backup);
	}

	@Override public void add(Appointment appointment) {
		if (!appointment.date.isPast()) {
			super.add(appointment);
		}
	}

	@Override public Appointment create(Map<String, String> parameter) {
		return new Appointment(parameter.get("name"), new Date(parameter.get("date")), parameter.get("attribute"));
	}

	@Override public Appointment create(NamedNodeMap nodeMap) {
		return new Appointment(	nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()),
								nodeMap.getNamedItem("attribute").getNodeValue());
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
}
