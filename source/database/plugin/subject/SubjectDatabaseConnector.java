package database.plugin.subject;

import java.security.InvalidParameterException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class SubjectDatabaseConnector implements IDatabaseConnector<Subject> {
	@Override
	public Subject create(ResultSet resultSet) throws SQLException {
		return new Subject(resultSet.getString("name"), resultSet.getDouble("score"), resultSet.getDouble("maxPoints"),
				resultSet.getInt("counter"));
	}

	@Override
	public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.SUBJECT_DELETE;
			case INSERT:
				return SQLStatements.SUBJECT_INSERT;
			case SELECT:
				return SQLStatements.SUBJECT_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override
	public PreparedStatement prepareStatement(Subject subject, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, subject.getName());
		preparedStatement.setDouble(2, subject.getScore());
		preparedStatement.setDouble(3, subject.getMaxPoints());
		preparedStatement.setInt(4, subject.getCounter());
		return preparedStatement;
	}
}
