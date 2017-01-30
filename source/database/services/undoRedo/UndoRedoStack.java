package database.services.undoRedo;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.services.ServiceRegistry;
import database.services.undoRedo.command.UndoableCommand;

public class UndoRedoStack implements IUndoRedo {
	private BoundedStack<UndoableCommand>	redoStack;
	private BoundedStack<UndoableCommand>	undoStack;

	public UndoRedoStack(int limit) {
		undoStack = new BoundedStack<>(limit);
		redoStack = new BoundedStack<>(limit);
	}

	@Override public void addUndoableCommand(UndoableCommand command) {
		undoStack.push(command);
		redoStack.clear();
	}

	@Override public boolean canRedo() {
		return !redoStack.isEmpty();
	}

	@Override public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	@Override public void redoCommand() throws SQLException, BadLocationException, InterruptedException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		UndoableCommand command = redoStack.pop();
		command.execute();
		terminal.update();
		terminal.printLine(command.executeLog(), StringType.REQUEST, StringFormat.ITALIC);
		terminal.waitForInput();
		undoStack.push(command);
	}

	@Override public void undoCommand() throws SQLException, InterruptedException, BadLocationException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		UndoableCommand command = undoStack.pop();
		command.revert();
		terminal.update();
		terminal.printLine(command.revertLog(), StringType.REQUEST, StringFormat.ITALIC);
		terminal.waitForInput();
		redoStack.push(command);
	}
}
