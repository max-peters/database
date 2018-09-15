package database.main;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;

import database.main.userInterface.GraphicalUserInterface;
import database.main.userInterface.ITerminal;
import database.main.userInterface.Terminal;
import database.plugin.stromWasser.StromWasserAbrechnungPlugin;
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
		try {
			Logger logger = Logger.Instance();
			IDatabase database = new MySQLDatabase();
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			Administration administration = new Administration();
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
			IWriterReader writerReader = new XmlWriterReader(
					System.getProperty("user.home") + "/Documents/storage.xml");
			ISettingsProvider settingsProvider = new SettingsProvider();
			IConnectorRegistry connectorRegistry = new ConnectorRegistry();
			ITerminal terminal = new Terminal(graphicalUserInterface);
			IPluginRegistry pluginRegistry = new HashMapPluginRegistry();
			IUndoRedo undoRedoService = new UndoRedoStack();
			IFrequentStringComplete frequentStringComplement = new ResultSetFrequentStringComplete();
			StromWasserAbrechnungPlugin stromwasser = new StromWasserAbrechnungPlugin();
			ServiceRegistry.Instance().register(ISettingsProvider.class, settingsProvider);
			ServiceRegistry.Instance().register(ITerminal.class, terminal);
			ServiceRegistry.Instance().register(IWriterReader.class, writerReader);
			ServiceRegistry.Instance().register(IPluginRegistry.class, pluginRegistry);
			ServiceRegistry.Instance().register(IUndoRedo.class, undoRedoService);
			ServiceRegistry.Instance().register(IConnectorRegistry.class, connectorRegistry);
			ServiceRegistry.Instance().register(IFrequentStringComplete.class, frequentStringComplement);
			logger.log("services registered");
			writerReader.register("settings", settingsProvider);
			writerReader.register("abrechnung", stromwasser);
			pluginRegistry.register(stromwasser);
			logger.log("plugins registered");
			writerReader.read();
			logger.log("internal parameters loaded");
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