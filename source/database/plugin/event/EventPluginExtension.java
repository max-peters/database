package database.plugin.event;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
		map.put("date", null);
		request(map);
		createAndAdd(map);
		update();
	}

	@Override public void print(Document document, Element element) {
		for (Event event : getIterable()) {
			Element entryElement = document.createElement(getIdentity());
			event.insertParameter(entryElement);
			element.appendChild(entryElement);
		}
	}
}
