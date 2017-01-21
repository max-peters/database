package database.main;

import java.lang.reflect.InvocationTargetException;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import database.main.autocompletition.HashMapAutocomplete;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.pluginRegistry.IPluginRegistry;
import database.services.undoRedo.CancelHelper;
import database.services.undoRedo.IUndoRedo;

public class Administration {
	public void request() throws Exception {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IPluginRegistry pluginRegistry = ServiceRegistry.Instance().get(IPluginRegistry.class);
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		IUndoRedo undoRedo = ServiceRegistry.Instance().get(IUndoRedo.class);
		String commands = pluginRegistry.getPluginNameTagsAsRegex("cancel", "exit", "save");
		HashMapAutocomplete autocomplete = new HashMapAutocomplete(commands);
		CancelHelper helper = new CancelHelper();
		autocomplete.add("expense");
		String command = null;
		while (true) {
			try {
				command = terminal.request("command", commands, autocomplete, 2);
				switch (command) {
					case "save":
						save();
						terminal.waitForInput();
						break;
					case "exit":
						database.close();
						System.exit(0);
						break;
					case "cancel":
						helper.cancel(undoRedo);
						break;
					default:
						Plugin plugin = pluginRegistry.getPlugin(command);
						String regex = plugin.getCommandTags(plugin.getClass());
						if (plugin.getCommandTags(plugin.getOutputHandler().getClass()).equals("()")) {
							regex = regex.replace("show", "").replace("||", "|");
						}
						command = terminal.request(command, regex, new HashMapAutocomplete(regex));
						plugin.conduct(command);
				}
			}
			catch (InvocationTargetException | UserCancelException e) {
				if (e.getClass().equals(UserCancelException.class)	|| e.getCause().getClass().equals(UserCancelException.class)
					|| e.getCause().getCause() != null && e.getCause().getCause().getClass().equals(UserCancelException.class)) {
					continue;
				}
				else {
					throw e;
				}
			}
		}
	}

	private void save() throws BadLocationException, TransformerException, ParserConfigurationException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IPluginRegistry pluginRegistry = ServiceRegistry.Instance().get(IPluginRegistry.class);
		terminal.blockInput();
		terminal.printLine("saving", StringType.REQUEST, StringFormat.ITALIC);
		pluginRegistry.write();
		terminal.printLine("saved", StringType.REQUEST, StringFormat.ITALIC);
	}
}