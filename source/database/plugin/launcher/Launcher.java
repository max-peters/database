package database.plugin.launcher;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import database.main.GraphicalUserInterface;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Pair;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	GitInterface	git;
	Terminal		terminal;

	public Launcher(GraphicalUserInterface graphicalUserInterface, Terminal terminal) throws IOException {
		super("launcher", null, graphicalUserInterface);
		git = new GitInterface();
		this.terminal = terminal;
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException, InterruptedException {
		graphicalUserInterface.blockInput();
		terminal.requestOut("pushing...");
		long time = System.currentTimeMillis();
		git.push();
		time = System.currentTimeMillis() - time;
		terminal.requestOut("finished pushing in " + time + " ms");
		graphicalUserInterface.waitForInput();
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException, InterruptedException {
		graphicalUserInterface.blockInput();
		terminal.requestOut("pulling...");
		long time = System.currentTimeMillis();
		git.pull();
		time = System.currentTimeMillis() - time;
		terminal.requestOut("finished pulling in " + time + " ms");
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

	@Override public List<Pair> write() {
		return null;
	}

	@Override public void create(Pair pair) {
	}
}