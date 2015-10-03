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

public class Main {
	public static void main(String[] args) {
		try {
			PluginContainer pluginContainer = new PluginContainer();
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			Terminal terminal = new Terminal(graphicalUserInterface);
			WriterReader writerReader = new WriterReader(pluginContainer);
			Administration administration = new Administration(graphicalUserInterface, pluginContainer, terminal, writerReader);
			Launcher launcher = new Launcher(graphicalUserInterface, terminal, administration);
			Storage storage = new Storage(pluginContainer, administration, graphicalUserInterface);
			SubjectPlugin subjectPlugin = new SubjectPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
			ExpensePlugin expensePlugin = new ExpensePlugin(pluginContainer, terminal, graphicalUserInterface, administration);
			RefillingPlugin refillingPlugin = new RefillingPlugin(pluginContainer, terminal, graphicalUserInterface, administration, (ExpenseList) expensePlugin.getInstanceList());
			TaskPlugin taskPlugin = new TaskPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
			EventPlugin eventPlugin = new EventPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
			pluginContainer.addPlugin(launcher);
			pluginContainer.addPlugin(storage);
			pluginContainer.addPlugin(subjectPlugin);
			pluginContainer.addPlugin(expensePlugin);
			pluginContainer.addPlugin(refillingPlugin);
			pluginContainer.addPlugin(taskPlugin);
			pluginContainer.addPlugin(eventPlugin);
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
			administration.initialOutput();
			graphicalUserInterface.show();
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
