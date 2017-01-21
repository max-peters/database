package database.plugin.connector;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Task;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class TaskDatabaseConnector implements IDatabaseConnector<Task> {
	@Override public Task create(ResultSet resultSet) throws SQLException {
		return new Task(resultSet.getString("name"));
	}

	@Override public PreparedStatement prepareStatement(Task task, QueryType type) throws SQLException {
		String sql = null;
		PreparedStatement preparedStatement;
		switch (type) {
			case DELETE:
				sql = SQLStatements.TASK_DELETE;
				break;
			case INSERT:
				sql = SQLStatements.TASK_INSERT;
				break;
			default:
				throw new InvalidParameterException();
		}
		preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, task.name);
		return preparedStatement;
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.TASK_SELECT;
	}
}
