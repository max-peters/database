package database.plugin.utility;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import database.main.PluginContainer;
import database.main.WriterReader;
import database.main.date.Date;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.storage.Storage;

public class UtilityPlugin extends Plugin {
	private News			news;
	private PluginContainer	pluginContainer;
	private Storage			storage;
	private WriterReader	writerReader;

	public UtilityPlugin(WriterReader writerReader, PluginContainer pluginContainer, Storage storage) throws IOException {
		super("utility");
		this.writerReader = writerReader;
		this.pluginContainer = pluginContainer;
		this.storage = storage;
		news = new News();
	}

	@Command(tag = "days") public void calculateDayNumber() throws BadLocationException, InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("first date", null);
		map.put("second date", null);
		request(map);
		Date firstDate = new Date(map.get("first date"));
		Date secondDate = new Date(map.get("second date"));
		Terminal.printLine("day number between " + firstDate + " and " + secondDate + ":", StringType.REQUEST, StringFormat.STANDARD);
		Terminal.printLine(Math.abs(firstDate.compareTo(secondDate)), StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}

	@Override public void initialOutput() throws BadLocationException {
		Terminal.printLine(news.getCurrentRank(), StringType.MAIN, StringFormat.STANDARD);
	}

	@Command(tag = "update") public void updateStorage()	throws BadLocationException, InterruptedException, ParserConfigurationException, SAXException, TransformerException,
															IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
															NoSuchMethodException, SecurityException {
		Terminal.printLine("updating...", StringType.REQUEST, StringFormat.ITALIC);
		Terminal.blockInput();
		for (Plugin plugin : pluginContainer.getPlugins()) {
			if (plugin instanceof InstancePlugin) {
				InstancePlugin current = (InstancePlugin) plugin;
				current.clearList();
			}
		}
		storage.clearList();
		writerReader.updateStorage();
		Terminal.update();
	}
}
