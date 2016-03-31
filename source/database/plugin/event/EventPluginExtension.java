package database.plugin.event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.InstancePlugin;
import database.plugin.PrintInformation;
import database.plugin.storage.Storage;

public abstract class EventPluginExtension<T extends Event> extends InstancePlugin<T> {
	public EventPluginExtension(String identity, Storage storage) {
		super(identity, storage, null);
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

	@Override public List<PrintInformation> print() {
		List<PrintInformation> printInformationList = new ArrayList<PrintInformation>();
		for (Event event : getIterable()) {
			printInformationList.add(new PrintInformation(getIdentity(), event.getParameter()));
		}
		return printInformationList;
	}
}
