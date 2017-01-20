package database.plugin.connector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Subject;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.SQLStatements;

public class SubjectDatabaseConnector implements IDatabaseConnector<Subject> {
	@Override public Subject create(ResultSet resultSet) throws SQLException {
		return new Subject(resultSet.getString("name"), resultSet.getDouble("score"), resultSet.getDouble("maxPoints"), resultSet.getInt("counter"));
	}

	@Override public PreparedStatement deleteQuery(Subject delete) throws SQLException {
		return prepareStatement(delete, SQLStatements.SUBJECT_DELETE);
	}

	@Override public PreparedStatement insertQuery(Subject insert) throws SQLException {
		return prepareStatement(insert, SQLStatements.SUBJECT_INSERT);
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.SUBJECT_SELECT;
	}

	private PreparedStatement prepareStatement(Subject subject, String sql) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, subject.name);
		preparedStatement.setDouble(2, subject.score);
		preparedStatement.setDouble(3, subject.maxPoints);
		preparedStatement.setInt(4, subject.counter);
		return preparedStatement;
	}
}
