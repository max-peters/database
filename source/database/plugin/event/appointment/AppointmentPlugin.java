package database.plugin.event.appointment;

import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.date.Time;
import database.main.userInterface.Terminal;
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
		return new Appointment(	parameter.get("name"), new Date(parameter.get("date")), parameter.get("begin").isEmpty() ? null : new Time(parameter.get("begin")),
								parameter.get("end").isEmpty() ? null : new Time(parameter.get("end")));
	}

	@Override public Appointment create(NamedNodeMap nodeMap) {
		return new Appointment(	nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()),
								nodeMap.getNamedItem("begin").getNodeValue().isEmpty() ? null : new Time(nodeMap.getNamedItem("begin").getNodeValue()),
								nodeMap.getNamedItem("end").getNodeValue().isEmpty() ? null : new Time(nodeMap.getNamedItem("end").getNodeValue()));
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		String name;
		String temp = "";
		Time begin;
		Date date;
		name = Terminal.request("name", ".+");
		date = new Date(Terminal.request("date", "DATE"));
		temp = Terminal.request("begin", "TIME");
		begin = temp.isEmpty() ? null : new Time(temp);
		if (begin != null) {
			temp = Terminal.request("end", "TIME");
			while (!temp.isEmpty() && begin.compareTo(new Time(temp)) <= 0) {
				Terminal.errorMessage();
				temp = Terminal.request("end", "TIME");
			}
		}
		add(new Appointment(name, date, begin, temp.isEmpty() ? null : new Time(temp)));
		update();
	}
}
