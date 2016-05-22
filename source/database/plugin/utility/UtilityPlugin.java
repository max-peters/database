package database.plugin.utility;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import database.main.WriterReader;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.Plugin;

public class UtilityPlugin extends Plugin {
	private News			news;
	private WriterReader	writerReader;

	public UtilityPlugin(Backup backup, WriterReader writerReader) throws IOException {
		super("utility", backup);
		this.news = new News();
		this.writerReader = writerReader;
	}

	@Command(tag = "days") public void calculateDayNumber() throws BadLocationException, InterruptedException {
		LocalDate firstDate = LocalDate.parse(Terminal.request("first date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		LocalDate secondDate = LocalDate.parse(Terminal.request("second date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Terminal.printLine("day number between "+ firstDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + " and "
							+ secondDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + ":", StringType.REQUEST, StringFormat.STANDARD);
		Terminal.printLine(ChronoUnit.DAYS.between(firstDate, secondDate), StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}

	@Command(tag = "status") public void getLoLStatus() throws BadLocationException, InterruptedException {
		Terminal.blockInput();
		Thread gatherer = new Thread(() -> {
			try {
				news.setDaysTillDecay();
				news.setRank();
				news.connection = true;
			}
			catch (Exception e) {
				news.connection = false;
			}
		});
		gatherer.start();
		Terminal.getLineOfCharacters('-', StringType.MAIN);
		Terminal.printLine("league of legends status:", StringType.MAIN, StringFormat.STANDARD);
		while (gatherer.isAlive()) {
			Terminal.printLine("fetching in progress |", StringType.REQUEST, StringFormat.STANDARD);
			Thread.sleep(100);
			Terminal.printLine("fetching in progress /", StringType.REQUEST, StringFormat.STANDARD);
			Thread.sleep(100);
			Terminal.printLine("fetching in progress \u2014", StringType.REQUEST, StringFormat.STANDARD);
			Thread.sleep(100);
		}
		if (news.connection) {
			Terminal.printLine(" " + news.getRank() + " [" + news.getDaysTillDecay() + " days till decay]", StringType.REQUEST, StringFormat.STANDARD);
			Terminal.waitForInput();
			Terminal.update();
		}
		else {
			Terminal.printLine(" error 404 page not found", StringType.REQUEST, StringFormat.STANDARD);
			Terminal.waitForInput();
			Terminal.update();
		}
	}

	@Command(tag = "update") public void updateStorage()	throws BadLocationException, InterruptedException, IOException, SAXException, TransformerException,
															ParserConfigurationException {
		Terminal.printLine("updating...", StringType.REQUEST, StringFormat.ITALIC);
		Terminal.blockInput();
		backup.clear();
		writerReader.updateStorage();
		Terminal.update();
	}
}
