package database.services.undoRedo.command;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import database.plugin.Instance;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;

public class DependingInsertCommand extends UndoableCommand {
	private List<Instance>	dependingInstances;
	private Instance		instance;

	public DependingInsertCommand(Instance i, Instance... instances) {
		instance = i;
		dependingInstances = new LinkedList<>(Arrays.asList(instances));
	}

	public void addDependingInstances(Instance i) {
		dependingInstances.add(i);
	}

	@Override public void execute() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		database.insert(instance);
		for (Instance instance : dependingInstances) {
			database.insert(instance);
		}
	}

	@Override public String executeLog() {
		String log = "inserted " + instance.toString();
		for (Instance instance : dependingInstances) {
			log += System.lineSeparator() + System.lineSeparator() + "inserted " + System.lineSeparator() + instance.toString();
		}
		return log;
	}

	@Override public void revert() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		database.remove(instance);
		for (Instance instance : dependingInstances) {
			database.remove(instance);
		}
	}

	@Override public String revertLog() {
		String log = "deleted " + System.lineSeparator() + instance.toString();
		for (Instance instance : dependingInstances) {
			log += System.lineSeparator() + System.lineSeparator() + "deleted " + System.lineSeparator() + instance.toString();
		}
		return log;
	}
}
