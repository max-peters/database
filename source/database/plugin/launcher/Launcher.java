package database.plugin.launcher;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Plugin;

public class Launcher extends Plugin {
	private News	news;
	private String	rank;

	public Launcher() throws IOException {
		super("launcher");
		news = new News();
		rank = news.getCurrentRank();
	}

	@Override public void initialOutput() throws BadLocationException {
		Terminal.printLine(rank, StringType.MAIN, StringFormat.STANDARD);
	}
}