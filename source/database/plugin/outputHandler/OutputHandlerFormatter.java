package database.plugin.outputHandler;

import java.sql.SQLException;
import database.plugin.IOutputHandler;
import database.plugin.connector.TaskDatabaseConnector;
import database.plugin.element.Task;
import database.services.ServiceRegistry;
import database.services.database.ConnectorRegistry;
import database.services.stringUtility.Builder;

public class OutputHandlerFormatter implements IOutputHandler {
	@Override public String getInitialOutput() throws SQLException {
		ConnectorRegistry registry = ServiceRegistry.Instance().get(ConnectorRegistry.class);
		TaskDatabaseConnector taskConnector = (TaskDatabaseConnector) registry.get(Task.class);
		Builder builder = new Builder();
		for (Task task : taskConnector.getList()) {
			builder.append(" \u2610 " + task.name);
			builder.newLine();
		}
		return builder.build();
	}
}
