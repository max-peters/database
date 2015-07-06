package database.main;

import java.io.IOException;
import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.date.Date;
import database.plugin.Plugin;

// TODO: stundenplan
public class Administration {
	private GraphicalUserInterface	graphicalUserInterface;
	private Store					store;
	private Terminal				terminal;
	private Launcher				launcher;
	private WriterReader			writerReader;

	public Administration(GraphicalUserInterface graphicalUserInterface, Store store, Terminal terminal, WriterReader writerReader, Launcher launcher) {
		this.graphicalUserInterface = graphicalUserInterface;
		this.store = store;
		this.terminal = terminal;
		this.writerReader = writerReader;
		this.launcher = launcher;
	}

	public void main() throws IOException, InterruptedException {
		initiation();
		while (true) {
			inputRequestAdministration();
		}
	}

	private void initiation() throws IOException, InterruptedException {
		connect();
		graphicalUserInterface.initialise();
		writerReader.read();
		initialOutput();
		graphicalUserInterface.show();
	}

	private void initialOutput() {
		for (Plugin plugin : store.getPlugins()) {
			plugin.initialOutput();
		}
	}

	private void connect() throws IOException, InterruptedException {
		if (!writerReader.checkDirectory()) {
			launcher.connect();
		}
	}

	private void inputRequestAdministration() throws InterruptedException, IOException {
		while (true) {
			String command = null;
			try {
				command = request("command", store.getNameTagsAsRegex().replace(")", "|") + "check|exit|save|push|pull)");
				if (command.matches(store.getNameTagsAsRegex())) {
					Plugin plugin = store.getPlugin(command);
					try {
						command = request(command, plugin.getCommandTags());
						plugin.conduct(command);
					}
					catch (NotImplementedException e) {
						errorMessage();
					}
				}
				else if (command.matches("(check|exit|save|push|pull)")) {
					switch (command) {
						case "check":
							store.getPlugin("task").remove(store.getPlugin("task").check());
							break;
						case "save":
							save();
							break;
						case "pull":
							launcher.pull();
							break;
						case "push":
							launcher.push();
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
	}

	private void save() throws IOException, InterruptedException {
		graphicalUserInterface.blockInput();
		writerReader.write();
		store.setChanges(false);
		terminal.requestOut("saved");
		graphicalUserInterface.waitForInput();
	}

	private void exit() throws IOException, InterruptedException {
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