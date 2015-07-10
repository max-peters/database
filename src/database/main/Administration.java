package database.main;

import java.io.IOException;
import java.util.concurrent.CancellationException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import database.main.date.Date;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

// TODO: stundenplan
public class Administration {
	private GraphicalUserInterface	graphicalUserInterface;
	private Store					store;
	private Terminal				terminal;
	private WriterReader			writerReader;

	public Administration(GraphicalUserInterface graphicalUserInterface, Store store, Terminal terminal, WriterReader writerReader) {
		this.graphicalUserInterface = graphicalUserInterface;
		this.store = store;
		this.terminal = terminal;
		this.writerReader = writerReader;
	}

	public void main() throws IOException, InterruptedException, SAXException, ParserConfigurationException, TransformerException {
		initiation();
		while (true) {
			inputRequestAdministration();
		}
	}

	private void initiation() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
		connect();
		graphicalUserInterface.initialise();
		writerReader.initialise();
		writerReader.read();
		initialOutput();
		graphicalUserInterface.show();
	}

	private void initialOutput() {
		for (Plugin plugin : store.getPlugins()) {
			if (plugin instanceof InstancePlugin) {
				((InstancePlugin) plugin).initialOutput();
			}
		}
	}

	private void connect() throws IOException, InterruptedException {
		if (!writerReader.checkDirectory()) {
			Process connection = Runtime.getRuntime().exec("cmd.exe /c net use Z: https://webdav.hidrive.strato.com/users/maxptrs/Server /user:maxptrs ***REMOVED*** /persistent:no");
			if (connection.waitFor() != 0) {
				throw new IOException("Error: server unavailable");
			}
		}
	}

	private void inputRequestAdministration() throws InterruptedException, IOException, ParserConfigurationException, TransformerException {
		String command = null;
		try {
			command = request("command", store.getPluginNameTagsAsRegesx().replace(")", "|") + "check|exit|save)");
			if (command.matches(store.getPluginNameTagsAsRegesx())) {
				Plugin plugin = store.getPlugin(command);
				command = request(command, plugin.getCommandTags());
				plugin.conduct(command);
			}
			else if (command.matches("(check|exit|save|push|pull)")) {
				switch (command) {
					case "check":
						((InstancePlugin) store.getPlugin("task")).remove(((InstancePlugin) store.getPlugin("task")).check());
						break;
					case "save":
						save();
						break;
					case "exit":
						exit();
						break;
				}
			}
		}
		catch (CancellationException e) {
			return;
		}
	}

	private void save() throws IOException, InterruptedException, ParserConfigurationException, TransformerException {
		graphicalUserInterface.blockInput();
		writerReader.write();
		store.setChanges(false);
		terminal.requestOut("saved");
		graphicalUserInterface.waitForInput();
	}

	private void exit() throws IOException, InterruptedException, ParserConfigurationException, TransformerException {
		if (store.getChanges()) {
			String command;
			command = request("there are unsaved changes - exit", "(y|n|s)");
			switch (command) {
				case "y":
					System.exit(0);
					break;
				case "n":
					break;
				case "s":
					graphicalUserInterface.blockInput();
					terminal.requestOut("saving");
					writerReader.write();
					System.exit(0);
					break;
			}
		}
		else {
			System.exit(0);
		}
	}

	public String request(String printOut, String regex) throws InterruptedException, CancellationException {
		boolean request = true;
		String result = null;
		String input = null;
		while (request) {
			terminal.requestOut(printOut + ":");
			input = terminal.in();
			if (input.compareTo("back") == 0) {
				throw new CancellationException();
			}
			else if ((regex != null) && (input.matches(regex))) {
				result = input;
				request = false;
			}
			else if ((regex == null) && (Date.testDateString(input))) {
				result = input;
				request = false;
			}
			else {
				errorMessage();
			}
		}
		return result;
	}

	public void errorMessage() throws InterruptedException {
		terminal.requestOut("invalid input");
		graphicalUserInterface.waitForInput();
	}

	public void update() {
		graphicalUserInterface.clear();
		initialOutput();
		store.setChanges(true);
	}
}