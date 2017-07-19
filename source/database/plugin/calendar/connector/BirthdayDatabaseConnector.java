package database.plugin.calendar.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.plugin.calendar.element.Birthday;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class BirthdayDatabaseConnector implements IDatabaseConnector<Birthday> {
	@Override
	public Birthday create(ResultSet resultSet) throws SQLException {
		return new Birthday(resultSet.getString("name"), resultSet.getDate("date").toLocalDate());
	}

	@Override
	public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.EVENT_DELETE;
			case INSERT:
				return SQLStatements.EVENT_INSERT;
			case SELECT:
				return SQLStatements.BIRTHDAY_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override
	public PreparedStatement prepareStatement(Birthday birthday, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, birthday.getName());
		preparedStatement.setDate(2, Date.valueOf(birthday.getDate()));
		preparedStatement.setString(3, "birthday");
		return preparedStatement;
	}
}
