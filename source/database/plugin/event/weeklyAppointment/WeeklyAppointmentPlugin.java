package database.plugin.event.weeklyAppointment;

import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.date.Time;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class WeeklyAppointmentPlugin extends EventPluginExtension<WeeklyAppointment> {
	public WeeklyAppointmentPlugin(Storage storage, Backup backup) {
		super("weeklyappointment", storage, backup);
	}

	@Override public void add(WeeklyAppointment weeklyAppointment) {
		while (!(!weeklyAppointment.date.isPast() && !weeklyAppointment.date.isToday()|| weeklyAppointment.date.isToday() && weeklyAppointment.begin == null
					|| weeklyAppointment.date.isToday() && weeklyAppointment.begin != null && weeklyAppointment.end == null && !weeklyAppointment.begin.isPast()
					|| weeklyAppointment.date.isToday() && weeklyAppointment.begin != null && weeklyAppointment.end != null && !weeklyAppointment.end.isPast())) {
			weeklyAppointment.date.addDays(7);;
		}
		super.add(weeklyAppointment);
	}

	@Override public WeeklyAppointment create(NamedNodeMap nodeMap) {
		return new WeeklyAppointment(	nodeMap.getNamedItem("name").getNodeValue(), new Date(nodeMap.getNamedItem("date").getNodeValue()),
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
		temp = Terminal.request("begin", "(TIME|)");
		begin = temp.isEmpty() ? null : new Time(temp);
		if (begin != null) {
			temp = Terminal.request("end", "(TIME|)");
			while (!temp.isEmpty() && begin.compareTo(new Time(temp)) <= 0) {
				Terminal.errorMessage();
				temp = Terminal.request("end", "(TIME|)");
			}
		}
		Terminal.blockInput();
		add(new WeeklyAppointment(name, date, begin, temp.isEmpty() ? null : new Time(temp)));
	}
}
