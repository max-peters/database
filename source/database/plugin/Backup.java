package database.plugin;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import database.main.PluginContainer;
import database.main.WriterReader;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;

public class Backup {
	private Document		backup;
	private boolean			changes	= false;
	private PluginContainer	pluginContainer;
	private Storage			storage;
	private WriterReader	writerReader;

	public Backup(WriterReader writerReader, PluginContainer pluginContainer, Storage storage) {
		this.writerReader = writerReader;
		this.pluginContainer = pluginContainer;
		this.storage = storage;
	}

	public void backup() {
		try {
			backup = writerReader.createDocument();
		}
		catch (ParserConfigurationException e) {}
		changes = true;
	}

	public void clear() throws BadLocationException {
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin instanceof InstancePlugin) {
				((InstancePlugin<?>) plugin).clearList();
			}
		}
		storage.clearList();
	}

	public boolean getChanges() {
		return changes;
	}

	public void restore() throws InterruptedException, IOException, SAXException, TransformerException, BadLocationException, ParserConfigurationException {
		if (backup != null) {
			Document temp = writerReader.createDocument();
			clear();
			writerReader.readDocument(backup);
			Terminal.update();
			backup = temp;
		}
		else {
			Terminal.printLine("nothing to cancel", StringType.SOLUTION, StringFormat.STANDARD);
			Terminal.waitForInput();
		}
	}

	public void save() {
		backup = null;
		changes = false;
	}
}