package database.plugin.launcher;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import database.main.Terminal;
import database.main.GraphicalUserInterface.StringFormat;
import database.main.GraphicalUserInterface.StringType;
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

	@Override public void initialOutput() throws BadLocationException {
		Terminal.printLine(rank, StringType.REQUEST, StringFormat.STANDARD);
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException, InterruptedException, BadLocationException {
		Terminal.blockInput();
		Terminal.printLine("pulling...", StringType.REQUEST, StringFormat.STANDARD);
		long time = System.currentTimeMillis();
		git.pull();
		time = System.currentTimeMillis() - time;
		Terminal.printLine("finished pulling in " + time + " ms", StringType.REQUEST, StringFormat.STANDARD);
		Terminal.waitForInput();
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException, InterruptedException, BadLocationException {
		Terminal.blockInput();
		Terminal.printLine("pushing...", StringType.REQUEST, StringFormat.STANDARD);
		long time = System.currentTimeMillis();
		git.push();
		time = System.currentTimeMillis() - time;
		Terminal.printLine("finished pushing in " + time + " ms", StringType.REQUEST, StringFormat.STANDARD);
		Terminal.waitForInput();
	}
}