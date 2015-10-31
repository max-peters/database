package database.plugin.launcher;

import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	private GitInterface	git;
	private News			news;
	private String			rank;

	public Launcher(GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super("launcher", administration, graphicalUserInterface);
		this.git = new GitInterface();
		this.news = new News();
		this.rank = news.getCurrentRank();
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException, InterruptedException {
		graphicalUserInterface.blockInput();
		Terminal.printRequest("pushing...");
		long time = System.currentTimeMillis();
		git.push();
		time = System.currentTimeMillis() - time;
		Terminal.printRequest("finished pushing in " + time + " ms");
		graphicalUserInterface.waitForInput();
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException, InterruptedException {
		graphicalUserInterface.blockInput();
		Terminal.printRequest("pulling...");
		long time = System.currentTimeMillis();
		git.pull();
		time = System.currentTimeMillis() - time;
		Terminal.printRequest("finished pulling in " + time + " ms");
		graphicalUserInterface.waitForInput();
	}

	@Override public String initialOutput() {
		return rank;
	}
}