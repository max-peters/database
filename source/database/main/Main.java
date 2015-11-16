package database.main;

import java.io.IOException;
import javax.swing.JOptionPane;
import database.plugin.event.EventPlugin;
import database.plugin.expense.ExpenseList;
import database.plugin.expense.ExpensePlugin;
import database.plugin.launcher.Launcher;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.storage.Storage;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.TaskPlugin;
import database.plugin.utility.UtilityPlugin;

public class Main {
	public static void main(String[] args) {
		try {
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			PluginContainer pluginContainer = new PluginContainer();
			new Terminal(graphicalUserInterface, pluginContainer);
			WriterReader writerReader = new WriterReader(pluginContainer);
			Administration administration = new Administration(pluginContainer, writerReader);
			Launcher launcher = new Launcher();
			Storage storage = new Storage(pluginContainer);
			SubjectPlugin subjectPlugin = new SubjectPlugin(pluginContainer);
			ExpensePlugin expensePlugin = new ExpensePlugin(pluginContainer);
			RefillingPlugin refillingPlugin = new RefillingPlugin(pluginContainer, (ExpenseList) expensePlugin.getInstanceList());
			TaskPlugin taskPlugin = new TaskPlugin(pluginContainer);
			EventPlugin eventPlugin = new EventPlugin(pluginContainer);
			UtilityPlugin utilityPlugin = new UtilityPlugin();
			pluginContainer.addPlugin(launcher);
			pluginContainer.addPlugin(storage);
			pluginContainer.addPlugin(subjectPlugin);
			pluginContainer.addPlugin(expensePlugin);
			pluginContainer.addPlugin(refillingPlugin);
			pluginContainer.addPlugin(taskPlugin);
			pluginContainer.addPlugin(eventPlugin);
			pluginContainer.addPlugin(utilityPlugin);
			if (!writerReader.checkDirectory()) {
				Process connection = Runtime.getRuntime().exec("cmd.exe /c net use Z: https://webdav.hidrive.strato.com/users/maxptrs/Server /user:maxptrs ***REMOVED*** /persistent:no");
				if (connection.waitFor() != 0) {
					writerReader.setDirectory();
				}
			}
			try {
				writerReader.read();
			}
			catch (IOException e) {
				graphicalUserInterface.showMessageDialog(e);
			}
			Terminal.initialOutput();
			expensePlugin.initialise();
			graphicalUserInterface.setVisible(true);
			graphicalUserInterface.setLocation();
			Terminal.printCollectedLines();
			administration.request();
		}
		catch (Throwable e) {
			String stackTrace = "";
			for (StackTraceElement element : e.getStackTrace()) {
				stackTrace = stackTrace + "\r\n" + element;
			}
			JOptionPane.showMessageDialog(null, stackTrace, e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}