package database.plugin.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.PrintInformation;

public class Storage extends Plugin {
	private ArrayList<String> storage;

	public Storage() {
		super("storage");
		storage = new ArrayList<String>();
	}

	public void clearList() {
		storage.clear();
	}

	@Override public void display() {
		// nothing to display
	}

	public ArrayList<String> getStorage() {
		return storage;
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (String string : storage) {
			PrintInformation pair = new PrintInformation("entry");
			pair.put("string", string);
			list.add(pair);
		}
		return list;
	}

	@Override public void read(PrintInformation pair) {
		storage.addAll(pair.getMap().values());
	}

	public void store(InstancePlugin instancePlugin) throws BadLocationException, InterruptedException {
		String line;
		for (Instance instance : instancePlugin.getInstanceList().getIterable()) {
			line = "";
			for (Entry<String, String> entry : instance.getParameter().entrySet()) {
				line += entry.getKey() + ": " + entry.getValue() + ", ";
			}
			storage.add(line.substring(0, line.lastIndexOf(",")));
		}
		instancePlugin.clearList();
		instancePlugin.update();
	}
}
