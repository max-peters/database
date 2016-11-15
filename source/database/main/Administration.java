package database.main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CancellationException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.FormatterProvider;
import database.plugin.Plugin;
import database.plugin.backup.BackupService;

public class Administration {
	public void request(ITerminal terminal, BackupService backupService, WriterReader writerReader, PluginContainer pluginContainer,
						FormatterProvider formatterProvider) throws Exception {
		while (true) {
			inputRequestAdministration(terminal, backupService, writerReader, pluginContainer, formatterProvider);
		}
	}

	private void exit(	ITerminal terminal, BackupService backupService, WriterReader writerReader,
						PluginContainer pluginContainer) throws InterruptedException, BadLocationException, TransformerException, ParserConfigurationException {
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
											FormatterProvider formatterProvider) throws Exception {
		String command = null;
		try {
			command = terminal.request("command", pluginContainer.getPluginNameTagsAsRegesx(new ArrayList<>(Arrays.asList(new String[] { "cancel", "exit", "save" }))), 2);
			if (command.matches(pluginContainer.getPluginNameTagsAsRegesx(new ArrayList<String>()))) {
				Plugin plugin = pluginContainer.getPlugin(command);
				command = terminal.request(command, plugin.getCommandTags(plugin.getClass()));
				plugin.conduct(command, terminal, backupService, pluginContainer, writerReader, formatterProvider);
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
						backupService.restore(terminal, pluginContainer, formatterProvider);
						break;
				}
			}
		}
		catch (InvocationTargetException | CancellationException e) {
			if (e.getClass().equals(CancellationException.class)	|| e.getCause().getClass().equals(CancellationException.class)
				|| e.getCause().getCause().getClass().equals(CancellationException.class)) {
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