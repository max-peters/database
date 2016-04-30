package database.plugin;

import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;

public class Storage {
	private ArrayList<String> storage;

	public Storage() {
		storage = new ArrayList<String>();
	}

	public void clearList() {
		storage.clear();
	}

	public void print(Document document, Element element) {
		for (String string : storage) {
			Element entryElement = document.createElement("entry");
			entryElement.setAttribute("string", string);
			element.appendChild(entryElement);
		}
	}

	public void read(String nodeName, NamedNodeMap nodeMap) {
		storage.add(nodeMap.getNamedItem("string").getNodeValue());
	}

	public void store(InstancePlugin<? extends Instance> instancePlugin) throws BadLocationException, InterruptedException {
		for (Instance instance : instancePlugin.getIterable()) {
			storage.add(instance.toString().replace("\"", "'"));
		}
		instancePlugin.clearList();
		Terminal.update();
	}
}
