package database.services.undoRedo.command;

import java.sql.SQLException;
import database.plugin.Instance;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;

public class DeleteCommand extends UndoableCommand {
	private Instance instance;

	public DeleteCommand(Instance i) {
		instance = i;
	}

	@Override public void execute() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		database.remove(instance);
	}

	@Override public String executeLog() {
		return "deleted" + System.lineSeparator() + instance.toString();
	}

	@Override public void revert() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		database.insert(instance);
	}

	@Override public String revertLog() {
		return "inserted" + System.lineSeparator() + instance.toString();
	}
}
