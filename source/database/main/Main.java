package database.main;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;
import database.main.userInterface.GraphicalUserInterface;
import database.main.userInterface.ITerminal;
import database.main.userInterface.Terminal;
import database.plugin.calendar.CalendarPlugin;
import database.plugin.calendar.connector.AppointmentDatabaseConnector;
import database.plugin.calendar.connector.BirthdayDatabaseConnector;
import database.plugin.calendar.connector.DayDatabaseConnector;
import database.plugin.calendar.connector.HolidayDatabaseConnector;
import database.plugin.calendar.element.Appointment;
import database.plugin.calendar.element.Birthday;
import database.plugin.calendar.element.Day;
import database.plugin.calendar.element.Holiday;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpenseDatabaseConnector;
import database.plugin.expense.ExpensePlugin;
import database.plugin.expense.repetitive.RepetitiveExpense;
import database.plugin.expense.repetitive.RepetitiveExpenseDatabaseConnector;
import database.plugin.expense.repetitive.RepetitiveExpensePlugin;
import database.plugin.refilling.Refilling;
import database.plugin.refilling.RefillingDatabaseConnector;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.subject.Subject;
import database.plugin.subject.SubjectDatabaseConnector;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.Task;
import database.plugin.task.TaskDatabaseConnector;
import database.plugin.task.TaskPlugin;
import database.services.ServiceRegistry;
import database.services.database.ConnectorRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;
import database.services.database.MySQLDatabase;
import database.services.pluginRegistry.HashMapPluginRegistry;
import database.services.pluginRegistry.IPluginRegistry;
import database.services.settings.ISettingsProvider;
import database.services.settings.SettingsProvider;
import database.services.stringComplete.IFrequentStringComplete;
import database.services.stringComplete.ResultSetFrequentStringComplete;
import database.services.undoRedo.IUndoRedo;
import database.services.undoRedo.UndoRedoStack;
import database.services.writerReader.IWriterReader;
import database.services.writerReader.XmlWriterReader;

public class Main {
	public static void main(String[] args) {
		Thread guiThread;
		Thread databaseThread;
		try {
			Logger logger = Logger.Instance();
			IDatabase database = new MySQLDatabase();
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			Administration administration = new Administration();
			databaseThread = new Thread(() -> {
				try {
					database.connect();
					logger.log("database connected");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			databaseThread.start();
			guiThread = new Thread(() -> {
				try {
					graphicalUserInterface.initialise();
					logger.log("graphical user interface initialised");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			guiThread.start();
			logger.log("threads started");
			IWriterReader writerReader = new XmlWriterReader(System.getProperty("user.home") + "/Documents/storage.xml");
			ISettingsProvider settingsProvider = new SettingsProvider();
			IConnectorRegistry connectorRegistry = new ConnectorRegistry();
			ITerminal terminal = new Terminal(graphicalUserInterface);
			IPluginRegistry pluginRegistry = new HashMapPluginRegistry();
			IUndoRedo undoRedoService = new UndoRedoStack();
			IFrequentStringComplete frequentStringComplement = new ResultSetFrequentStringComplete();
			ServiceRegistry.Instance().register(ISettingsProvider.class, settingsProvider);
			ServiceRegistry.Instance().register(IDatabase.class, database);
			ServiceRegistry.Instance().register(ITerminal.class, terminal);
			ServiceRegistry.Instance().register(IWriterReader.class, writerReader);
			ServiceRegistry.Instance().register(IPluginRegistry.class, pluginRegistry);
			ServiceRegistry.Instance().register(IUndoRedo.class, undoRedoService);
			ServiceRegistry.Instance().register(IConnectorRegistry.class, connectorRegistry);
			ServiceRegistry.Instance().register(IFrequentStringComplete.class, frequentStringComplement);
			logger.log("services registered");
			connectorRegistry.register(Appointment.class, new AppointmentDatabaseConnector());
			connectorRegistry.register(Birthday.class, new BirthdayDatabaseConnector());
			connectorRegistry.register(Day.class, new DayDatabaseConnector());
			connectorRegistry.register(Holiday.class, new HolidayDatabaseConnector());
			connectorRegistry.register(Expense.class, new ExpenseDatabaseConnector());
			connectorRegistry.register(Refilling.class, new RefillingDatabaseConnector());
			connectorRegistry.register(RepetitiveExpense.class, new RepetitiveExpenseDatabaseConnector());
			connectorRegistry.register(Subject.class, new SubjectDatabaseConnector());
			connectorRegistry.register(Task.class, new TaskDatabaseConnector());
			SubjectPlugin subjectPlugin = new SubjectPlugin();
			ExpensePlugin expensePlugin = new ExpensePlugin();
			RepetitiveExpensePlugin repetitiveExpensePlugin = new RepetitiveExpensePlugin();
			RefillingPlugin refillingPlugin = new RefillingPlugin();
			TaskPlugin taskPlugin = new TaskPlugin();
			CalendarPlugin calendarPlugin = new CalendarPlugin();
			pluginRegistry.register(calendarPlugin);
			pluginRegistry.register(taskPlugin);
			pluginRegistry.register(subjectPlugin);
			pluginRegistry.register(expensePlugin);
			pluginRegistry.register(repetitiveExpensePlugin);
			pluginRegistry.register(refillingPlugin);
			writerReader.register(calendarPlugin.identity, calendarPlugin);
			writerReader.register(subjectPlugin.identity, subjectPlugin);
			writerReader.register(refillingPlugin.identity, refillingPlugin);
			writerReader.register(taskPlugin.identity, taskPlugin);
			writerReader.register("settings", settingsProvider);
			logger.log("plugins registered");
			writerReader.read();
			logger.log("internal parameters loaded");
			databaseThread.join();
			((ExpenseDatabaseConnector) ServiceRegistry.Instance().get(IConnectorRegistry.class).get(Expense.class)).refreshStringComplete();
			ServiceRegistry.Instance().get(IFrequentStringComplete.class).create("appointment");
			calendarPlugin.updateCalendar();
			repetitiveExpensePlugin.createExpense();
			guiThread.join();
			graphicalUserInterface.setVisible(true);
			logger.log("initialisation complete");
			logger.print();
			terminal.update();
			administration.request();
		}
		catch (Throwable e) {
			String stackTrace = "";
			if (e instanceof InvocationTargetException) {
				e = e.getCause();
			}
			for (StackTraceElement element : e.getStackTrace()) {
				stackTrace = stackTrace + System.getProperty("line.separator") + element;
			}
			JOptionPane.showMessageDialog(null, stackTrace, e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
	}
}