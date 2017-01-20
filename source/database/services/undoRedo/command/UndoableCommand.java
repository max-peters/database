package database.services.undoRedo.command;

import java.sql.SQLException;

public abstract class UndoableCommand {
	public abstract void execute() throws SQLException;

	public abstract String executeLog();

	public abstract void revert() throws SQLException;

	public abstract String revertLog();
}
