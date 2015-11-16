package database.plugin.launcher;

import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	private GitInterface	git;
	private News			news;
	private String			rank;

	public Launcher() throws IOException {
		super("launcher");
		git = new GitInterface();
		news = new News();
		rank = news.getCurrentRank();
	}

	@Override public String initialOutput() {
		return rank;
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException, InterruptedException {
		Terminal.blockInput();
		Terminal.printRequest("pulling...");
		long time = System.currentTimeMillis();
		git.pull();
		time = System.currentTimeMillis() - time;
		Terminal.printRequest("finished pulling in " + time + " ms");
		Terminal.waitForInput();
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException, InterruptedException {
		Terminal.blockInput();
		Terminal.printRequest("pushing...");
		long time = System.currentTimeMillis();
		git.push();
		time = System.currentTimeMillis() - time;
		Terminal.printRequest("finished pushing in " + time + " ms");
		Terminal.waitForInput();
	}
}