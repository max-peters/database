package database.services.undoRedo.command;

import java.sql.SQLException;

import database.plugin.Instance;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.stringUtility.Builder;

public class ChangeCommand extends UndoableCommand {
	private Instance changed;
	private Instance unchanged;

	public ChangeCommand(Instance unchanged, Instance changed) {
		this.unchanged = unchanged;
		this.changed = changed;
	}

	@Override
	public void execute() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		database.remove(unchanged);
		database.insert(changed);
	}

	@Override
	public String executeLog() {
		Builder builder = new Builder();
		builder.append("changed");
		builder.newLine();
		builder.append(unchanged.toString());
		builder.newLine();
		builder.append("to");
		builder.newLine();
		builder.append(changed.toString());
		return builder.build();
	}

	@Override
	public void revert() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		database.remove(changed);
		database.insert(unchanged);
	}

	@Override
	public String revertLog() {
		Builder builder = new Builder();
		builder.append("changed");
		builder.newLine();
		builder.append(changed.toString());
		builder.newLine();
		builder.append("to");
		builder.newLine();
		builder.append(unchanged.toString());
		return builder.build();
	}
}
