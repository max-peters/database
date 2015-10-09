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
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			graphicalUserInterface.setProgress(0);
			PluginContainer pluginContainer = new PluginContainer();
			WriterReader writerReader = new WriterReader(pluginContainer);
			graphicalUserInterface.setProgress(20);
			Administration administration = new Administration(graphicalUserInterface, pluginContainer, writerReader);
			Launcher launcher = new Launcher(graphicalUserInterface, administration);
			Storage storage = new Storage(pluginContainer, administration, graphicalUserInterface);
			SubjectPlugin subjectPlugin = new SubjectPlugin(pluginContainer, graphicalUserInterface, administration);
			ExpensePlugin expensePlugin = new ExpensePlugin(pluginContainer, graphicalUserInterface, administration);
			graphicalUserInterface.setProgress(40);
			RefillingPlugin refillingPlugin = new RefillingPlugin(pluginContainer, graphicalUserInterface, administration, (ExpenseList) expensePlugin.getInstanceList());
			TaskPlugin taskPlugin = new TaskPlugin(pluginContainer, graphicalUserInterface, administration);
			EventPlugin eventPlugin = new EventPlugin(pluginContainer, graphicalUserInterface, administration);
			Terminal.setGraphicalUserInterface(graphicalUserInterface);
			graphicalUserInterface.setProgress(60);
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
			graphicalUserInterface.setProgress(80);
			try {
				writerReader.read();
			}
			catch (IOException e) {
				graphicalUserInterface.showMessageDialog(e);
			}
			graphicalUserInterface.setProgress(90);
			expensePlugin.initialise();
			administration.initialOutput();
			graphicalUserInterface.show();
			Terminal.startOut();
			graphicalUserInterface.setProgress(100);
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