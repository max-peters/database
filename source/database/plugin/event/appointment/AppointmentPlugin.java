package database.plugin.event.appointment;

import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.date.Time;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class AppointmentPlugin extends EventPluginExtension<Appointment> {
	public AppointmentPlugin(Storage storage, Backup backup) {
		super("appointment", storage, backup);
	}

	@Override public void add(Appointment appointment) {
		if (!appointment.date.isPast() && !appointment.date.isToday()
			|| appointment.date.isToday() && appointment.begin != null && appointment.end == null && !appointment.begin.isPast()
			|| appointment.date.isToday() && appointment.begin != null && appointment.end != null && !appointment.end.isPast()) {
			super.add(appointment);
		}
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
		temp = Terminal.request("begin", "TIMEn");
		begin = temp.isEmpty() ? null : new Time(temp);
		if (begin != null) {
			temp = Terminal.request("end", "TIMEn");
			while (!temp.isEmpty() && begin.compareTo(new Time(temp)) <= 0) {
				Terminal.errorMessage();
				temp = Terminal.request("end", "TIMEn");
			}
		}
		add(new Appointment(name, date, begin, temp.isEmpty() ? null : new Time(temp)));
	}
}
