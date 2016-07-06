package database.plugin.utility;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import database.main.PluginContainer;
import database.main.WriterReader;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.FormatterProvider;
import database.plugin.Plugin;

public class UtilityPlugin extends Plugin {
	private News news;

	public UtilityPlugin() throws IOException {
		super("utility");
		news = new News();
	}

	@Command(tag = "days") public void calculateDayNumber(ITerminal terminal) throws BadLocationException, InterruptedException {
		String temp = terminal.request("first date", "DATE");
		LocalDate firstDate = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		temp = terminal.request("second date", "DATE");
		LocalDate secondDate = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		terminal.printLine("day number between "+ firstDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + " and "
							+ secondDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + ":", StringType.REQUEST, StringFormat.STANDARD);
		terminal.printLine(ChronoUnit.DAYS.between(firstDate, secondDate), StringType.SOLUTION, StringFormat.STANDARD);
		terminal.waitForInput();
	}

	@Command(tag = "status") public void getLoLStatus(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider)	throws BadLocationException,
																																				InterruptedException {
		terminal.blockInput();
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
		terminal.getLineOfCharacters('-', StringType.MAIN);
		terminal.printLine("league of legends status:", StringType.MAIN, StringFormat.STANDARD);
		while (gatherer.isAlive()) {
			terminal.printLine("fetching in progress |", StringType.REQUEST, StringFormat.ITALIC);
			Thread.sleep(100);
			terminal.printLine("fetching in progress /", StringType.REQUEST, StringFormat.ITALIC);
			Thread.sleep(100);
			terminal.printLine("fetching in progress \u2014", StringType.REQUEST, StringFormat.ITALIC);
			Thread.sleep(100);
		}
		if (news.connection) {
			terminal.printLine(" " + news.getRank() + " [" + news.getDaysTillDecay() + " days till decay]", StringType.REQUEST, StringFormat.STANDARD);
			terminal.waitForInput();
			terminal.update(pluginContainer, formatterProvider);
		}
		else {
			terminal.printLine(" error 404 page not found", StringType.REQUEST, StringFormat.STANDARD);
			terminal.waitForInput();
			terminal.update(pluginContainer, formatterProvider);
		}
	}

	@Override public void initialOutput(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException {
		// no initial output
	}

	@Override public void print(Document document, Element appendTo) {
		// no printing
	}

	@Override public void read(Node node) throws ParserConfigurationException {
		// no reading
	}

	@Command(tag = "update") public void updateStorage(	ITerminal terminal, PluginContainer pluginContainer, WriterReader writerReader,
														FormatterProvider formatterProvider)	throws BadLocationException, InterruptedException, IOException, SAXException,
																								TransformerException, ParserConfigurationException {
		terminal.printLine("connecting...", StringType.REQUEST, StringFormat.ITALIC);
		terminal.blockInput();
		writerReader.updateStorage(terminal, pluginContainer);
		terminal.waitForInput();
		terminal.update(pluginContainer, formatterProvider);
	}
}
