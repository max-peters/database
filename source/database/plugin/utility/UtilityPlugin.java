package database.plugin.utility;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import database.main.WriterReader;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.Plugin;

public class UtilityPlugin extends Plugin {
	private Thread			guiThread;
	private News			news;
	private WriterReader	writerReader;
	private Thread			launcher;

	public UtilityPlugin(Backup backup, WriterReader writerReader, News news, Thread guiThread) throws IOException {
		super("utility", backup);
		this.news = news;
		this.writerReader = writerReader;
		this.guiThread = guiThread;
	}

	@Command(tag = "days") public void calculateDayNumber() throws BadLocationException, InterruptedException {
		LocalDate firstDate = LocalDate.parse(Terminal.request("first date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		LocalDate secondDate = LocalDate.parse(Terminal.request("second date", "DATE"), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Terminal.printLine("day number between "+ firstDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + " and "
							+ secondDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + ":", StringType.REQUEST, StringFormat.STANDARD);
		Terminal.printLine(ChronoUnit.DAYS.between(firstDate, secondDate), StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}

	@Override public void initialOutput() throws BadLocationException {
		if (news.getDaysTillDecay() != 0) {
			Terminal.printLine(news.getRank() + "  (" + news.getDaysTillDecay() + " days till decay)", StringType.MAIN, StringFormat.STANDARD);
		}
		else {
			Terminal.printLine("fetching in progress...", StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void print(Document document, Element appendTo) {
		Element entryElement = document.createElement("display");
		entryElement.setTextContent(String.valueOf(getDisplay()));
		appendTo.appendChild(entryElement);
	}

	@Override public void read(Node node) throws ParserConfigurationException, DOMException {
		if (node.getNodeName().equals("display")) {
			setDisplay(Boolean.valueOf(node.getTextContent()));
		}
	}

	@Override public void setDisplay(boolean display) {
		super.setDisplay(display);
		if (display == true) {
			launcher = new Thread(() -> {
				try {
					news.setRank();
					news.setDaysTillDecay();
					guiThread.join();
					Terminal.update();
					Terminal.printLine("command:", StringType.REQUEST, StringFormat.ITALIC);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			launcher.start();
		}
	};

	@Command(tag = "update") public void updateStorage()	throws BadLocationException, InterruptedException, IOException, SAXException, TransformerException,
															ParserConfigurationException {
		Terminal.printLine("updating...", StringType.REQUEST, StringFormat.ITALIC);
		Terminal.blockInput();
		backup.clear();
		writerReader.updateStorage();
		Terminal.update();
	}
}
