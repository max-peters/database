package database.plugin.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.RequestInformation;

public class Storage extends Plugin {
	private ArrayList<String> storage;

	public Storage() {
		super("storage");
		storage = new ArrayList<String>();
	}

	@Override public List<RequestInformation> getInformationList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		for (String string : storage) {
			RequestInformation pair = new RequestInformation("entry");
			pair.put("string", string);
			list.add(pair);
		}
		return list;
	}

	public ArrayList<String> getStorage() {
		return storage;
	}

	@Override public void readInformation(RequestInformation pair) {
		storage.addAll(pair.getMap().values());
	}

	public void store(InstancePlugin instancePlugin) throws BadLocationException, InterruptedException {
		String line;
		for (Instance instance : instancePlugin.getList()) {
			line = "";
			for (Entry<String, String> entry : instance.getParameter().entrySet()) {
				line += entry.getKey() + ": " + entry.getValue() + ", ";
			}
			storage.add(line.substring(0, line.lastIndexOf(",")));
		}
		instancePlugin.clearList();;
		instancePlugin.update();
	}

	public void clearList() {
		storage.clear();
	}

	public void display() {
		// nothing to display
	}
}
