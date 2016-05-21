package database.plugin.event.multiDayAppointment;

import javax.swing.text.BadLocationException;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class MultiDayAppointmentPlugin extends EventPluginExtension<MultiDayAppointment> {
	public MultiDayAppointmentPlugin(Storage storage, Backup backup) {
		super("multidayappointment", storage, backup, MultiDayAppointment.class);
		// TODO Auto-generated constructor stub
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		// TODO Auto-generated method stub
	}
}
