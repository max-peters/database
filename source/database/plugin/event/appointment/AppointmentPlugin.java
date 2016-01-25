package database.plugin.event.appointment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.event.EventPluginExtention;
import database.plugin.storage.Storage;

public class AppointmentPlugin extends EventPluginExtention {
	public AppointmentPlugin(Storage storage) {
		super("appointment", new AppointmentList(), storage);
	}

	public void createRequest() throws InterruptedException, BadLocationException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", ".*");
		map.put("date", null);
		request(map);
		getInstanceList().add(map);
		update();
	}
}
