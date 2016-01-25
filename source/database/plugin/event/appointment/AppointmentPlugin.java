package database.plugin.event.appointment;

import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class AppointmentPlugin extends EventPluginExtention {
	public AppointmentPlugin(Storage storage) {
		super("appointment", new AppointmentList(), storage);
	}
}
