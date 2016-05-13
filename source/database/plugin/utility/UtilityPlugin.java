package database.plugin.utility;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
import database.main.WriterReader;
import database.main.date.Date;
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
		Date firstDate = new Date(Terminal.request("first date", "DATE"));
		Date secondDate = new Date(Terminal.request("second date", "DATE"));
		Terminal.printLine("day number between " + firstDate + " and " + secondDate + ":", StringType.REQUEST, StringFormat.STANDARD);
		Terminal.printLine(Math.abs(firstDate.compareTo(secondDate)), StringType.SOLUTION, StringFormat.STANDARD);
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

	@Override public void print(Document document, Element element) {
		Element entryElement = document.createElement("display");
		entryElement.setAttribute("boolean", String.valueOf(getDisplay()));
		element.appendChild(entryElement);
	}

	@Override public void read(String nodeName, NamedNodeMap nodeMap) throws ParserConfigurationException, DOMException {
		if (nodeName.equals("display")) {
			setDisplay(Boolean.valueOf(nodeMap.getNamedItem("boolean").getNodeValue()));
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
