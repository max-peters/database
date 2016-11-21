package database.plugin.settings;

import javax.swing.text.BadLocationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import database.main.PluginContainer;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.Plugin;

public class Settings extends Plugin {
	private int eventDisplayRange;

	public Settings() {
		super("settings");
	}

	@Override public void display(ITerminal terminal, PluginContainer pluginContainer) {
		// nothing to display
	}

	public int getDisplayedDays() {
		return eventDisplayRange;
	}

	@Override public void initialOutput(ITerminal terminal, PluginContainer pluginContainer) throws BadLocationException {
		// no initial output
	}

	@Override public void print(Document document, Element element) {
		Element entryElement = document.createElement("eventDisplayRange");
		entryElement.setTextContent(String.valueOf(eventDisplayRange));
		element.appendChild(entryElement);
	}

	@Override public void read(Node node) {
		if (node.getNodeName().equals("eventDisplayRange")) {
			eventDisplayRange = Integer.valueOf(node.getTextContent());
		}
	}

	@Command(tag = "days") public void setDisplayedDays(ITerminal terminal, PluginContainer pluginContainer)	throws InterruptedException, BadLocationException,
																												NumberFormatException, UserCancelException {
		eventDisplayRange = Integer.valueOf(terminal.request("enter event display range [days]", "[0-9]{1,13}"));
		terminal.update(pluginContainer);
	}
}
