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
		return new Appointment(parameter.get("name"), new Date(parameter.get("date")), new Time(parameter.get("begin")), new Time(parameter.get("end")));
	}

	@Override public Appointment create(NamedNodeMap nodeMap) {
		return new Appointment(	nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()),
								nodeMap.getNamedItem("begin").getNodeValue().isEmpty() ? null : new Time(nodeMap.getNamedItem("begin").getNodeValue()),
								nodeMap.getNamedItem("end").getNodeValue().isEmpty() ? null : new Time(nodeMap.getNamedItem("end").getNodeValue()));
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		String name;
		String end;
		Time begin;
		Date date;
		name = Terminal.request("name", ".+");
		date = new Date(Terminal.request("date", "DATE"));
		begin = new Time(Terminal.request("begin", "TIME"));
		end = Terminal.request("end", "TIME");
		while (!end.isEmpty() && begin.compareTo(new Time(end)) <= 0) {
			Terminal.errorMessage();
			end = Terminal.request("end", "TIME");
		}
		add(new Appointment(name, date, begin, end.isEmpty() ? null : new Time(end)));
		update();
	}
}
