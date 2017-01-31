package database.main;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;
import database.main.userInterface.GraphicalUserInterface;
import database.main.userInterface.ITerminal;
import database.main.userInterface.Terminal;
import database.plugin.connector.AppointmentDatabaseConnector;
import database.plugin.connector.BirthdayDatabaseConnector;
import database.plugin.connector.DayDatabaseConnector;
import database.plugin.connector.ExpenseDatabaseConnector;
import database.plugin.connector.HolidayDatabaseConnector;
import database.plugin.connector.RefillingDatabaseConnector;
import database.plugin.connector.RepetitiveExpenseDatabaseConnector;
import database.plugin.connector.SubjectDatabaseConnector;
import database.plugin.connector.TaskDatabaseConnector;
import database.plugin.element.Appointment;
import database.plugin.element.Birthday;
import database.plugin.element.Day;
import database.plugin.element.Expense;
import database.plugin.element.Holiday;
import database.plugin.element.Refilling;
import database.plugin.element.RepetitiveExpense;
import database.plugin.element.Subject;
import database.plugin.element.Task;
import database.plugin.plugin.CalendarPlugin;
import database.plugin.plugin.ExpensePlugin;
import database.plugin.plugin.RefillingPlugin;
import database.plugin.plugin.RepetitiveExpensePlugin;
import database.plugin.plugin.SubjectPlugin;
import database.plugin.plugin.TaskPlugin;
import database.services.ServiceRegistry;
import database.services.database.ConnectorRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;
import database.services.database.MySQLDatabase;
import database.services.pluginRegistry.HashMapPluginRegistry;
import database.services.pluginRegistry.IPluginRegistry;
import database.services.settings.Settings;
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
			Settings settings = new Settings();
			IConnectorRegistry connectorRegistry = new ConnectorRegistry();
			ITerminal terminal = new Terminal(graphicalUserInterface);
			IWriterReader writerReader = new XmlWriterReader(settings.storageDirectory);
			IPluginRegistry pluginRegistry = new HashMapPluginRegistry();
			IUndoRedo undoRedoService = new UndoRedoStack(settings.revertStackSize);
			IFrequentStringComplete frequentStringComplement = new ResultSetFrequentStringComplete();
			ServiceRegistry.Instance().register(Settings.class, settings);
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
			CalendarPlugin eventPlugin = new CalendarPlugin();
			pluginRegistry.register(eventPlugin);
			pluginRegistry.register(taskPlugin);
			pluginRegistry.register(subjectPlugin);
			pluginRegistry.register(expensePlugin);
			pluginRegistry.register(repetitiveExpensePlugin);
			pluginRegistry.register(refillingPlugin);
			logger.log("plugins registered");
			writerReader.read();
			databaseThread.join();
			((ExpenseDatabaseConnector) ServiceRegistry.Instance().get(IConnectorRegistry.class).get(Expense.class)).refreshStringComplete();
			ServiceRegistry.Instance().get(IFrequentStringComplete.class).create("appointment");
			eventPlugin.updateCalendar();
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