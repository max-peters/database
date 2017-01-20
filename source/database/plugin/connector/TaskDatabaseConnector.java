package database.plugin.connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Task;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.SQLStatements;

public class TaskDatabaseConnector implements IDatabaseConnector<Task> {
	@Override public Task create(ResultSet resultSet) throws SQLException {
		return new Task(resultSet.getString("name"));
	}

	@Override public PreparedStatement deleteQuery(Task delete) throws SQLException {
		return prepareStatement(delete, SQLStatements.TASK_DELETE);
	}

	@Override public PreparedStatement insertQuery(Task insert) throws SQLException {
		return prepareStatement(insert, SQLStatements.TASK_INSERT);
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.TASK_SELECT;
	}

	private PreparedStatement prepareStatement(Task task, String sql) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, task.name);
		return preparedStatement;
	}
}
