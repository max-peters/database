package database.main;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import database.main.userInterface.ITerminal;
import database.main.userInterface.OutputType;
import database.main.userInterface.StringFormat;
import database.plugin.Plugin;
import database.plugin.stromWasser.StromWasserAbrechnungPlugin;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.pluginRegistry.IPluginRegistry;
import database.services.stringComplete.HashMapStringComplete;
import database.services.undoRedo.CancelHelper;
import database.services.undoRedo.IUndoRedo;
import database.services.writerReader.IWriterReader;

public class Administration {
	public void request() throws Exception {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IPluginRegistry pluginRegistry = ServiceRegistry.Instance().get(IPluginRegistry.class);
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		IUndoRedo undoRedo = ServiceRegistry.Instance().get(IUndoRedo.class);
		String commands = pluginRegistry.getPluginNameTagsAsRegex("cancel", "exit", "save");
		HashMapStringComplete stringComplete = new HashMapStringComplete(commands);
		CancelHelper helper = new CancelHelper();
		stringComplete.add("expense");
		String command = null;
		Plugin plugin = pluginRegistry.getPlugin("abrechnung");
		while (true) {
			try {
				terminal.printLine("Drücke beliebige Taste um zu beginnen...", OutputType.CLEAR, StringFormat.STANDARD);
				terminal.waitForInput();
				((StromWasserAbrechnungPlugin) plugin).start();
			}
			catch (UserCancelException e) {
				continue;
			}
		}
	}

	private void save() throws BadLocationException, TransformerException, ParserConfigurationException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IWriterReader writerReader = ServiceRegistry.Instance().get(IWriterReader.class);
		terminal.blockInput();
		terminal.printLine("saving", OutputType.CLEAR, StringFormat.ITALIC);
		writerReader.write();
		terminal.printLine("saved", OutputType.CLEAR, StringFormat.ITALIC);
	}
}