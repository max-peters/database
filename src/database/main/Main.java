package database.main;

import java.io.IOException;

import javax.swing.JOptionPane;

import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.TaskPlugin;

public class Main {
	public static void main(String[] args) {
		Store store = new Store();
		Launcher launcher = new Launcher();
		GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
		Terminal terminal = new Terminal(graphicalUserInterface);
		WriterReader writerReader = new WriterReader(store);
		Administration administration = new Administration(graphicalUserInterface, store, terminal, writerReader, launcher);
		SubjectPlugin subject = new SubjectPlugin(store, terminal, graphicalUserInterface, administration);
		BirthdayPlugin birthday = new BirthdayPlugin(store, terminal, graphicalUserInterface, administration);
		RefillingPlugin refilling = new RefillingPlugin(store, terminal, graphicalUserInterface, administration);
		TaskPlugin task = new TaskPlugin(store, terminal, graphicalUserInterface, administration);
		ExpensePlugin expense = new ExpensePlugin(store, terminal, graphicalUserInterface, administration);
		store.addPlugin(expense);
		store.addPlugin(task);
		store.addPlugin(refilling);
		store.addPlugin(birthday);
		store.addPlugin(subject);
		try {
			administration.main();
		}
		catch (IOException | InterruptedException e) {
			JOptionPane.showMessageDialog(graphicalUserInterface.frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
