package database.main;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Plugin;

public class Administration {
	private PluginContainer	pluginContainer;
	private WriterReader	writerReader;

	public Administration(PluginContainer pluginContainer, WriterReader writerReader) {
		this.pluginContainer = pluginContainer;
		this.writerReader = writerReader;
	}

	public void request() throws Exception {
		while (true) {
			inputRequestAdministration();
		}
	}

	private void exit() throws InterruptedException, BadLocationException, ParserConfigurationException, TransformerException {
		if (pluginContainer.changes()) {
			String command;
			command = Terminal.request("there are unsaved changes - exit", "(y|n|s)");
			switch (command) {
				case "s":
					save();
					//$FALL-THROUGH$
				case "y":
					System.exit(0);
			}
		}
		else {
			System.exit(0);
		}
	}

	private void inputRequestAdministration() throws Exception {
		String command = null;
		try {
			command = Terminal.request("command", pluginContainer.getPluginNameTagsAsRegesx().replace(")", "|") + "exit|save)");
			if (command.matches(pluginContainer.getPluginNameTagsAsRegesx())) {
				Plugin plugin = pluginContainer.getPlugin(command);
				command = Terminal.request(command, plugin.getCommandTags(plugin.getClass()));
				plugin.conduct(command);
			}
			else if (command.matches("(exit|save)")) {
				switch (command) {
					case "save":
						save();
						Terminal.waitForInput();
						break;
					case "exit":
						exit();
						break;
				}
			}
		}
		catch (InvocationTargetException | CancellationException e) {
			if (e.getClass().equals(CancellationException.class) || e.getCause().getClass().equals(CancellationException.class)) {
				return;
			}
			else {
				throw e;
			}
		}
	}

	private void save() throws BadLocationException, ParserConfigurationException, TransformerException {
		Terminal.blockInput();
		Terminal.printLine("saving", StringType.REQUEST, StringFormat.ITALIC);
		writerReader.write();
		pluginContainer.setUnchanged();
		Terminal.printLine("saved", StringType.REQUEST, StringFormat.ITALIC);
	}
}