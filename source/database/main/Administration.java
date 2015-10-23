package database.main;

import java.awt.FontFormatException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CancellationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.jgit.api.errors.GitAPIException;
import database.main.date.Date;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;

public class Administration {
	private GraphicalUserInterface	graphicalUserInterface;
	private PluginContainer			pluginContainer;
	private WriterReader			writerReader;

	public Administration(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer, WriterReader writerReader) throws IOException, InterruptedException {
		this.graphicalUserInterface = graphicalUserInterface;
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
			command = request("command", pluginContainer.getPluginNameTagsAsRegesx().replace(")", "|") + "check|exit|save)");
			if (command.matches(pluginContainer.getPluginNameTagsAsRegesx())) {
				Plugin plugin = pluginContainer.getPlugin(command);
				command = request(command, plugin.getCommandTags());
				plugin.conduct(command);
			}
			else if (command.matches("(check|exit|save)")) {
				switch (command) {
					case "check":
						((InstancePlugin) pluginContainer.getPlugin("task")).remove(((InstancePlugin) pluginContainer.getPlugin("task")).check());
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
		graphicalUserInterface.blockInput();
		Terminal.requestOut("saving");
		writerReader.write();
		pluginContainer.setUnchanged();
		Terminal.requestOut("saved");
		graphicalUserInterface.waitForInput();
	}

	private void exit() throws IOException, InterruptedException, ParserConfigurationException, TransformerException {
		if (pluginContainer.getChanges()) {
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
					Terminal.requestOut("saving");
					writerReader.write();
					pluginContainer.setUnchanged();
					System.exit(0);
					break;
			}
		}
		else {
			System.exit(0);
		}
	}

	public String request(String printOut, String regex) throws InterruptedException {
		boolean request = true;
		String result = null;
		String input = null;
		while (request) {
			Terminal.requestOut(printOut + ":");
			input = Terminal.in();
			if (input.equals("back")) {
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
		Terminal.requestOut("invalid input");
		graphicalUserInterface.waitForInput();
	}

	public void initialOutput() {
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin.getDisplay()) {
				Terminal.out(plugin.initialOutput());
			}
		}
	}
}