package database.plugin.settings;

import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.Plugin;

public class Settings extends Plugin {
	private int eventDisplayRange;

	public Settings(Backup backup) {
		super("settings", backup);
	}

	@Override public void display() {
		// nothing to display
	}

	public int getDisplayedDays() {
		return eventDisplayRange;
	}

	@Override public void print(Document document, Element element) {
		Element entryElement = document.createElement("eventDisplayRange");
		entryElement.setAttribute("int", String.valueOf(eventDisplayRange));
		element.appendChild(entryElement);
	}

	@Override public void read(String nodeName, NamedNodeMap nodeMap) {
		if (nodeName.equals("eventDisplayRange")) {
			eventDisplayRange = Integer.valueOf(nodeMap.getNamedItem("int").getNodeValue());
		}
	}

	@Command(tag = "days") public void setDisplayedDays() throws InterruptedException, BadLocationException {
		eventDisplayRange = Integer.valueOf(Terminal.request("enter event display range [days]", "[0-9]{1,13}"));
		Terminal.update();
	}
}
