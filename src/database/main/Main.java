package database.main;

import javax.swing.JOptionPane;

import database.plugin.event.EventPlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.launcher.Launcher;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.TaskPlugin;

public class Main {
	public static void main(String[] args) {
		PluginContainer pluginContainer = new PluginContainer();
		GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
		Terminal terminal = new Terminal(graphicalUserInterface);
		WriterReader writerReader = new WriterReader(pluginContainer);
		Administration administration = new Administration(graphicalUserInterface, pluginContainer, terminal, writerReader);
		SubjectPlugin subject = new SubjectPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
		RefillingPlugin refilling = new RefillingPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
		TaskPlugin task = new TaskPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
		ExpensePlugin expense = new ExpensePlugin(pluginContainer, terminal, graphicalUserInterface, administration);
		EventPlugin event = new EventPlugin(pluginContainer, terminal, graphicalUserInterface, administration);
		Launcher launcher = new Launcher();
		pluginContainer.addPlugin(launcher);
		pluginContainer.addPlugin(subject);
		pluginContainer.addPlugin(refilling);
		pluginContainer.addPlugin(expense);
		pluginContainer.addPlugin(task);
		pluginContainer.addPlugin(event);
		try {
			administration.main();
		}
		catch (Throwable e) {
			JOptionPane.showMessageDialog(graphicalUserInterface.frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
