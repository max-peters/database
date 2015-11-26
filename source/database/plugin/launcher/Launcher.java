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
		try {
			rank = news.getCurrentRank();
		}
		catch (IOException e) {}
	}

	@Override public void initialOutput() throws BadLocationException {
		Terminal.printLine(rank, StringType.MAIN, StringFormat.STANDARD);
	}

	@Command(tag = "pull") public void pull() throws IOException, GitAPIException, InterruptedException, BadLocationException {
		Terminal.blockInput();
		Terminal.printLine("pulling...", StringType.SOLUTION, StringFormat.STANDARD);
		long time = System.currentTimeMillis();
		git.pull();
		time = System.currentTimeMillis() - time;
		Terminal.printLine("finished pulling in " + time + " ms", StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}

	@Command(tag = "push") public void push() throws IOException, GitAPIException, InterruptedException, BadLocationException {
		Terminal.blockInput();
		Terminal.printLine("pushing...", StringType.SOLUTION, StringFormat.STANDARD);
		long time = System.currentTimeMillis();
		git.push();
		time = System.currentTimeMillis() - time;
		Terminal.printLine("finished pushing in " + time + " ms", StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}
}