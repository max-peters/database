package database.services.undoRedo;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;
import database.main.userInterface.ITerminal;
import database.services.ServiceRegistry;
import database.services.undoRedo.command.UndoableCommand;

public class CommandHandler {
	private static CommandHandler instance;

	private CommandHandler() {}

	public void executeCommand(UndoableCommand command) throws SQLException, BadLocationException, InterruptedException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IUndoRedoService undoRedoService = ServiceRegistry.Instance().get(IUndoRedoService.class);
		if (command != null) {
			command.execute();
			undoRedoService.addUndoableCommand(command);
			terminal.update();
		}
	}

	public static CommandHandler Instance() {
		if (instance == null) {
			instance = new CommandHandler();
		}
		return instance;
	}
}