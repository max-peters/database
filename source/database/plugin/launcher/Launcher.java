package database.plugin.launcher;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

import database.main.GraphicalUserInterface;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	GitInterface			git;
	GraphicalUserInterface	graphicalUserInterface;
	Terminal				terminal;

	public Launcher(GraphicalUserInterface graphicalUserInterface, Terminal terminal) throws IOException {
		super("launcher", null);
		git = new GitInterface();
		this.terminal = terminal;
		this.graphicalUserInterface = graphicalUserInterface;
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException, InterruptedException {
		graphicalUserInterface.blockInput();
		terminal.requestOut("pushing...");
		git.push();
		terminal.requestOut("finished pushing");
		graphicalUserInterface.waitForInput();
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException, InterruptedException {
		graphicalUserInterface.blockInput();
		terminal.requestOut("pulling...");
		git.pull();
		terminal.requestOut("finished pulling");
		graphicalUserInterface.waitForInput();
	}

	@Override public void conduct(String command) throws InterruptedException, IOException, GitAPIException {
		switch (command) {
			case "pull":
				pull();
				break;
			case "push":
				push();
				break;
		}
	}
}