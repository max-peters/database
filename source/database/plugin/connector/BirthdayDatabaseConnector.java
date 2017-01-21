package database.plugin.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Birthday;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class BirthdayDatabaseConnector implements IDatabaseConnector<Birthday> {
	@Override public Birthday create(ResultSet resultSet) throws SQLException {
		return new Birthday(resultSet.getString("name"), resultSet.getDate("date").toLocalDate());
	}

	@Override public PreparedStatement prepareStatement(Birthday birthday, QueryType type) throws SQLException {
		String sql = null;
		PreparedStatement preparedStatement;
		switch (type) {
			case DELETE:
				sql = SQLStatements.EVENT_DELETE;
				break;
			case INSERT:
				sql = SQLStatements.EVENT_INSERT;
				break;
			default:
				throw new InvalidParameterException();
		}
		preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, birthday.getName());
		preparedStatement.setDate(2, Date.valueOf(birthday.getDate()));
		preparedStatement.setString(3, "birthday");
		return preparedStatement;
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.BIRTHDAY_SELECT;
	}
}
