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
			long time = System.currentTimeMillis();
			PluginContainer pluginContainer = new PluginContainer();
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			WriterReader writerReader = new WriterReader(pluginContainer);
			Administration administration = new Administration(graphicalUserInterface, pluginContainer, writerReader);
			Launcher launcher = new Launcher(graphicalUserInterface, administration);
			Storage storage = new Storage(pluginContainer, administration, graphicalUserInterface);
			SubjectPlugin subjectPlugin = new SubjectPlugin(pluginContainer, graphicalUserInterface, administration);
			ExpensePlugin expensePlugin = new ExpensePlugin(pluginContainer, graphicalUserInterface, administration);
			RefillingPlugin refillingPlugin = new RefillingPlugin(pluginContainer, graphicalUserInterface, administration, (ExpenseList) expensePlugin.getInstanceList());
			TaskPlugin taskPlugin = new TaskPlugin(pluginContainer, graphicalUserInterface, administration);
			EventPlugin eventPlugin = new EventPlugin(pluginContainer, graphicalUserInterface, administration);
			Terminal.setGraphicalUserInterface(graphicalUserInterface);
			System.out.println("initialised after " + String.valueOf(System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
			pluginContainer.addPlugin(launcher);
			pluginContainer.addPlugin(storage);
			pluginContainer.addPlugin(subjectPlugin);
			pluginContainer.addPlugin(expensePlugin);
			pluginContainer.addPlugin(refillingPlugin);
			pluginContainer.addPlugin(taskPlugin);
			pluginContainer.addPlugin(eventPlugin);
			System.out.println("plugins added after " + String.valueOf(System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
			if (!writerReader.checkDirectory()) {
				Process connection = Runtime.getRuntime().exec("cmd.exe /c net use Z: https://webdav.hidrive.strato.com/users/maxptrs/Server /user:maxptrs ***REMOVED*** /persistent:no");
				if (connection.waitFor() != 0) {
					writerReader.setDirectory();
				}
			}
			System.out.println("conected after " + String.valueOf(System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
			try {
				writerReader.read();
			}
			catch (IOException e) {
				graphicalUserInterface.showMessageDialog(e);
			}
			System.out.println("read finished after " + String.valueOf(System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
			expensePlugin.initialise();
			administration.initialOutput();
			graphicalUserInterface.show();
			Terminal.startOut();
			System.out.println("something after " + String.valueOf(System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
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