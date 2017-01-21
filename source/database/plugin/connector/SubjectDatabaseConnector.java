package database.plugin.connector;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Subject;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class SubjectDatabaseConnector implements IDatabaseConnector<Subject> {
	@Override public Subject create(ResultSet resultSet) throws SQLException {
		return new Subject(resultSet.getString("name"), resultSet.getDouble("score"), resultSet.getDouble("maxPoints"), resultSet.getInt("counter"));
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.SUBJECT_SELECT;
	}

	@Override public PreparedStatement prepareStatement(Subject subject, QueryType type) throws SQLException {
		String sql = null;
		PreparedStatement preparedStatement;
		switch (type) {
			case DELETE:
				sql = SQLStatements.SUBJECT_DELETE;
				break;
			case INSERT:
				sql = SQLStatements.SUBJECT_INSERT;
				break;
			default:
				throw new InvalidParameterException();
		}
		preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, subject.name);
		preparedStatement.setDouble(2, subject.score);
		preparedStatement.setDouble(3, subject.maxPoints);
		preparedStatement.setInt(4, subject.counter);
		return preparedStatement;
	}
}
