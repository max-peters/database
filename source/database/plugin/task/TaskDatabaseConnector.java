package database.plugin.task;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class TaskDatabaseConnector implements IDatabaseConnector<Task> {
	@Override
	public Task create(ResultSet resultSet) throws SQLException {
		return new Task(resultSet.getString("name"));
	}

	@Override
	public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.TASK_DELETE;
			case INSERT:
				return SQLStatements.TASK_INSERT;
			case SELECT:
				return SQLStatements.TASK_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override
	public PreparedStatement prepareStatement(Task task, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, task.getName());
		return preparedStatement;
	}
}
