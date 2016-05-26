package database.plugin.event.multiDayAppointment;

import javax.swing.text.BadLocationException;
import database.plugin.Storage;
import database.plugin.event.EventPluginExtension;

public class MultiDayAppointmentPlugin extends EventPluginExtension<MultiDayAppointment> {
	public MultiDayAppointmentPlugin(Storage storage) {
		super("multidayappointment", storage, MultiDayAppointment.class);
		// TODO Auto-generated constructor stub
	}

	@Override public void createRequest() throws InterruptedException, BadLocationException {
		// TODO Auto-generated method stub
	}
}
