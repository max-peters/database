package database.main;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Plugin;

public class Administration {
	private Backup			backup;
	private PluginContainer	pluginContainer;
	private WriterReader	writerReader;

	public Administration(PluginContainer pluginContainer, WriterReader writerReader, Backup backup) {
		this.pluginContainer = pluginContainer;
		this.writerReader = writerReader;
		this.backup = backup;
	}

	public void request() throws Exception {
		while (true) {
			inputRequestAdministration();
		}
	}

	private void exit() throws InterruptedException, BadLocationException, TransformerException, ParserConfigurationException {
		if (backup.getChanges()) {
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
			command = Terminal.request("command", pluginContainer.getPluginNameTagsAsRegesx().replace(")", "|") + "cancel|exit|save)");
			if (command.matches(pluginContainer.getPluginNameTagsAsRegesx())) {
				Plugin plugin = pluginContainer.getPlugin(command);
				command = Terminal.request(command, plugin.getCommandTags(plugin.getClass()));
				plugin.conduct(command);
			}
			else if (command.matches("(exit|cancel|save)")) {
				switch (command) {
					case "save":
						save();
						Terminal.waitForInput();
						break;
					case "exit":
						exit();
						break;
					case "cancel":
						backup.restore();
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

	private void save() throws BadLocationException, TransformerException, ParserConfigurationException {
		Terminal.blockInput();
		Terminal.printLine("saving", StringType.REQUEST, StringFormat.ITALIC);
		writerReader.write();
		backup.save();
		Terminal.printLine("saved", StringType.REQUEST, StringFormat.ITALIC);
	}
}