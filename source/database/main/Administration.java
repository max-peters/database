package database.main;

import java.lang.reflect.InvocationTargetException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import database.main.autocompletition.Autocomplete;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.Plugin;
import database.plugin.backup.BackupService;

public class Administration {
	public void request(ITerminal terminal, BackupService backupService, WriterReader writerReader, PluginContainer pluginContainer) throws Exception {
		Autocomplete autocomplete = new Autocomplete(pluginContainer.getPluginNameTagsAsRegesx(new String[] { "cancel", "exit", "save" }));
		autocomplete.add("expense");
		while (true) {
			inputRequestAdministration(terminal, backupService, writerReader, pluginContainer, autocomplete);
		}
	}

	private void exit(	ITerminal terminal, BackupService backupService, WriterReader writerReader,
						PluginContainer pluginContainer)	throws InterruptedException, BadLocationException, TransformerException, ParserConfigurationException,
															UserCancelException {
		if (backupService.isChanged()) {
			String command;
			command = terminal.request("there are unsaved changes - exit", "(y|n|s)");
			switch (command) {
				case "s":
					save(terminal, backupService, writerReader, pluginContainer);
					//$FALL-THROUGH$
				case "y":
					System.exit(0);
			}
		}
		else {
			System.exit(0);
		}
	}

	private void inputRequestAdministration(ITerminal terminal, BackupService backupService, WriterReader writerReader, PluginContainer pluginContainer,
											Autocomplete autocomplete) throws Exception {
		String command = null;
		try {
			command = terminal.request("command", pluginContainer.getPluginNameTagsAsRegesx(new String[] { "cancel", "exit", "save" }), autocomplete, 2);
			if (command.matches(pluginContainer.getPluginNameTagsAsRegesx(new String[] {}))) {
				Plugin plugin = pluginContainer.getPlugin(command);
				String regex = plugin.getCommandTags(plugin.getClass());
				command = terminal.request(command, regex, new Autocomplete(regex));
				plugin.conduct(command, terminal, backupService, pluginContainer, writerReader);
			}
			else if (command.matches("(exit|cancel|save)")) {
				switch (command) {
					case "save":
						save(terminal, backupService, writerReader, pluginContainer);
						terminal.waitForInput();
						break;
					case "exit":
						exit(terminal, backupService, writerReader, pluginContainer);
						break;
					case "cancel":
						backupService.restore(terminal, pluginContainer);
						break;
				}
			}
		}
		catch (InvocationTargetException | UserCancelException e) {
			if (e.getClass().equals(UserCancelException.class)	|| e.getCause().getClass().equals(UserCancelException.class)
				|| e.getCause().getCause() != null && e.getCause().getCause().getClass().equals(UserCancelException.class)) {
				return;
			}
			else {
				throw e;
			}
		}
	}

	private void save(	ITerminal terminal, BackupService backupService, WriterReader writerReader,
						PluginContainer pluginContainer) throws BadLocationException, TransformerException, ParserConfigurationException {
		terminal.blockInput();
		terminal.printLine("saving", StringType.REQUEST, StringFormat.ITALIC);
		writerReader.write(pluginContainer);
		backupService.save();
		terminal.printLine("saved", StringType.REQUEST, StringFormat.ITALIC);
	}
}