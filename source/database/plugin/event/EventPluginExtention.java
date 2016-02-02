package database.plugin.event;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Instance;
import database.plugin.InstanceList;
import database.plugin.InstancePlugin;
import database.plugin.PrintInformation;
import database.plugin.storage.Storage;

public class EventPluginExtention extends InstancePlugin {
	public EventPluginExtention(String identity, InstanceList instanceList, Storage storage) {
		super(identity, instanceList, storage);
	}

	public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
								InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)");
		map.put("date", null);
		request(map);
		getInstanceList().add(map);
		update();
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (Instance instance : getInstanceList().getIterable()) {
			list.add(new PrintInformation(getIdentity(), instance.getParameter()));
		}
		return list;
	}
}
