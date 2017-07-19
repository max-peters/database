package database.plugin.task;

import java.sql.SQLException;

import database.plugin.IOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.stringUtility.Builder;

public class TaskOutputHandler implements IOutputHandler {
	@Override
	public String getInitialOutput() throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		TaskDatabaseConnector taskConnector = (TaskDatabaseConnector) registry.get(Task.class);
		Builder builder = new Builder();
		for (Task task : taskConnector.getList()) {
			builder.append(" \u2610 " + task.getName());
			builder.newLine();
		}
		return builder.build();
	}
}
