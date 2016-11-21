package database.plugin;

import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import database.main.PluginContainer;
import database.main.userInterface.ITerminal;

public class Storage {
	private ArrayList<String> storage;

	public Storage() {
		storage = new ArrayList<>();
	}

	public void clearList() {
		storage.clear();
	}

	public void print(Document document, Element element) {
		for (String string : storage) {
			Element entryElement = document.createElement("entry");
			entryElement.setTextContent(string);
			element.appendChild(entryElement);
		}
	}

	public void read(Node node) {
		storage.add(node.getTextContent());
	}

	public void store(InstancePlugin<? extends Instance> instancePlugin, ITerminal terminal, PluginContainer pluginContainer) throws BadLocationException, InterruptedException {
		for (Instance instance : instancePlugin.getIterable()) {
			storage.add(instance.toString());
		}
		instancePlugin.clearList();
		terminal.update(pluginContainer);
	}
}
