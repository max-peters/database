package database.plugin.event;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Backup;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public abstract class EventPluginExtension<T extends Event> extends InstancePlugin<T> {
	public EventPluginExtension(String identity, Storage storage, Backup backup) {
		super(identity, storage, null, backup);
	}

	@Override public void add(T event) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.compareTo(event.date) > 0) {
			i--;
		}
		list.add(i, event);
	}

	public void createRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		map.put("date", "DATE");
		request(map);
		createAndAdd(map);
		update();
	}

	@Override public void display() throws InterruptedException, BadLocationException {
		super.display();
	}

	@Override public void initialOutput() throws BadLocationException {
		// no initial output here
	}

	@Override public void show() {
		// nothing to show here
	}

	@Override public void store() throws BadLocationException, InterruptedException {
		super.store();
	}
}
