package database.main;

import java.awt.FontFormatException;
import java.io.IOException;
import java.util.concurrent.CancellationException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.jgit.api.errors.GitAPIException;

import database.main.date.Date;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.launcher.News;

// TODO: stundenplan
public class Administration {
	private GraphicalUserInterface	graphicalUserInterface;
	private PluginContainer			pluginContainer;
	private Terminal				terminal;
	private WriterReader			writerReader;

	public Administration(GraphicalUserInterface graphicalUserInterface, PluginContainer pluginContainer, Terminal terminal, WriterReader writerReader) throws IOException, InterruptedException {
		this.graphicalUserInterface = graphicalUserInterface;
		this.pluginContainer = pluginContainer;
		this.terminal = terminal;
		this.writerReader = writerReader;
	}

	public void request() throws IOException, InterruptedException, ParserConfigurationException, TransformerException, FontFormatException, GitAPIException {
		while (true) {
			inputRequestAdministration();
		}
	}

	private void inputRequestAdministration() throws InterruptedException, IOException, ParserConfigurationException, TransformerException, GitAPIException {
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
		catch (CancellationException e) {
			return;
		}
	}

	private void save() throws IOException, InterruptedException, ParserConfigurationException, TransformerException {
		graphicalUserInterface.blockInput();
		writerReader.write();
		pluginContainer.setUnchanged();
		terminal.requestOut("saved");
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

	public void initialOutput() {
		try {
			terminal.out(new News().getCurrentRank());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin instanceof InstancePlugin) {
				((InstancePlugin) plugin).initialOutput();
			}
		}
	}
}