package database.main;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import database.plugin.event.EventPlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.launcher.Launcher;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.TaskPlugin;

public class Main {
	public static void main(String[] args) {
		Store store = new Store();
		GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
		Terminal terminal = new Terminal(graphicalUserInterface);
		WriterReader writerReader = new WriterReader(store);
		Administration administration = new Administration(graphicalUserInterface, store, terminal, writerReader);
		SubjectPlugin subject = new SubjectPlugin(store, terminal, graphicalUserInterface, administration);
		RefillingPlugin refilling = new RefillingPlugin(store, terminal, graphicalUserInterface, administration);
		TaskPlugin task = new TaskPlugin(store, terminal, graphicalUserInterface, administration);
		ExpensePlugin expense = new ExpensePlugin(store, terminal, graphicalUserInterface, administration);
		EventPlugin event = new EventPlugin(store, terminal, graphicalUserInterface, administration);
		Launcher launcher = new Launcher();
		store.addPlugin(launcher);
		store.addPlugin(subject);
		store.addPlugin(refilling);
		store.addPlugin(expense);
		store.addPlugin(task);
		store.addPlugin(event);
		try {
			administration.main();
		}
		catch (IOException | InterruptedException | SAXException | ParserConfigurationException | TransformerException e) {
			JOptionPane.showMessageDialog(graphicalUserInterface.frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
