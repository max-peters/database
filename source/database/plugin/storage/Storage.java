package database.plugin.storage;

import java.util.ArrayList;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

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

	@Override public void print(Document document, Element element) {
		for (String string : storage) {
			Element entryElement = document.createElement("entry");
			entryElement.setAttribute("string", string);
			element.appendChild(entryElement);
		}
	}

	@Override public void read(String nodeName, NamedNodeMap nodeMap) {
		storage.add(nodeMap.getNamedItem("string").getNodeValue());
	}

	public void store(InstancePlugin<? extends Instance> instancePlugin) throws BadLocationException, InterruptedException {
		String line;
		for (Instance instance : instancePlugin.getIterable()) {
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
