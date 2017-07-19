package database.services.undoRedo;

import java.sql.SQLException;

import javax.swing.text.BadLocationException;

import database.services.undoRedo.command.UndoableCommand;

public interface IUndoRedo {
	public void addUndoableCommand(UndoableCommand command);

	public boolean canRedo();

	public boolean canUndo();

	public void redoCommand() throws SQLException, BadLocationException, InterruptedException;

	public void undoCommand() throws SQLException, BadLocationException, InterruptedException;
}
