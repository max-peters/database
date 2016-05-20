package database.plugin.event.multiDayAppointment;

import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class MultiDayAppointmentPlugin extends EventPluginExtension<MultiDayAppointment> {
	public MultiDayAppointmentPlugin(Storage storage, Backup backup) {
		super("multidayappointment", storage, backup);
		// TODO Auto-generated constructor stub
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		// TODO Auto-generated method stub
	}

	@Override public MultiDayAppointment create(NamedNodeMap nodeMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
