package database.main;

import java.awt.FontFormatException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CancellationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.jgit.api.errors.GitAPIException;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

public class Administration {
	private PluginContainer	pluginContainer;
	private WriterReader	writerReader;

	public Administration(PluginContainer pluginContainer, WriterReader writerReader) throws IOException, InterruptedException {
		this.pluginContainer = pluginContainer;
		this.writerReader = writerReader;
	}

	public void request()	throws IOException, InterruptedException, ParserConfigurationException, TransformerException, FontFormatException, GitAPIException,
							IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		while (true) {
			inputRequestAdministration();
		}
	}

	private void inputRequestAdministration()	throws InterruptedException, IOException, TransformerException, GitAPIException, IllegalArgumentException,
												ParserConfigurationException, IllegalAccessException, InvocationTargetException {
		String command = null;
		try {
			command = Terminal.request("command", pluginContainer.getPluginNameTagsAsRegesx().replace(")", "|") + "check|exit|save)");
			if (command.matches(pluginContainer.getPluginNameTagsAsRegesx())) {
				Plugin plugin = pluginContainer.getPlugin(command);
				command = Terminal.request(command, plugin.getCommandTags());
				plugin.conduct(command);
			}
			else if (command.matches("(check|exit|save)")) {
				switch (command) {
					case "check":
						((InstancePlugin) pluginContainer.getPlugin("task")).remove(((InstancePlugin) pluginContainer.getPlugin("task")).check());
						break;
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
		catch (InvocationTargetException e) {
			if (e.getCause().getClass().equals(CancellationException.class)) {
				return;
			}
			else {
				throw e;
			}
		}
	}

	private void save() throws IOException, InterruptedException, ParserConfigurationException, TransformerException {
		Terminal.blockInput();
		Terminal.printRequest("saving");
		writerReader.write();
		pluginContainer.setUnchanged();
		Terminal.printRequest("saved");
	}

	private void exit() throws IOException, InterruptedException, ParserConfigurationException, TransformerException {
		if (pluginContainer.getChanges()) {
			String command;
			command = Terminal.request("there are unsaved changes - exit", "(y|n|s)");
			switch (command) {
				case "y":
					System.exit(0);
					break;
				case "n":
					break;
				case "s":
					save();
					System.exit(0);
					break;
			}
		}
		else {
			System.exit(0);
		}
	}
}