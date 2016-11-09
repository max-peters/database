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
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.Command;
import database.plugin.FormatterProvider;
import database.plugin.Plugin;

public class UtilityPlugin extends Plugin {
	public UtilityPlugin() throws IOException {
		super("utility");
	}

	@Command(tag = "days") public void calculateDayNumber(ITerminal terminal) throws BadLocationException, InterruptedException {
		String temp = terminal.request("first date", "DATE");
		LocalDate firstDate = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		temp = terminal.request("second date", "DATE");
		LocalDate secondDate = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		terminal.printLine("day number between "	+ firstDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + " and "
							+ secondDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) + ":", StringType.REQUEST, StringFormat.STANDARD);
		terminal.printLine(ChronoUnit.DAYS.between(firstDate, secondDate), StringType.SOLUTION, StringFormat.STANDARD);
		terminal.waitForInput();
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
