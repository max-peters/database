package database.main;

import database.main.userInterface.ITerminal;
import database.main.userInterface.OutputType;
import database.main.userInterface.StringFormat;
import database.plugin.Plugin;
import database.plugin.accounting.AccountingPlugin;
import database.services.ServiceRegistry;
import database.services.pluginRegistry.IPluginRegistry;
import database.services.stringComplete.HashMapStringComplete;

public class Administration {
	public void request() throws Exception {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IPluginRegistry pluginRegistry = ServiceRegistry.Instance().get(IPluginRegistry.class);
		String commands = pluginRegistry.getPluginNameTagsAsRegex("cancel", "exit", "save");
		HashMapStringComplete stringComplete = new HashMapStringComplete(commands);
		stringComplete.add("expense");
		Plugin plugin = pluginRegistry.getPlugin("accounting");
		while (true) {
			try {
				terminal.printLine("Drücke beliebige Taste um zu beginnen...", OutputType.CLEAR, StringFormat.STANDARD);
				terminal.waitForInput();
				((AccountingPlugin) plugin).start();
			}
			catch (UserCancelException e) {
				continue;
			}
		}
	}
}