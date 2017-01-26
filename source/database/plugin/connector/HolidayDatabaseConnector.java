package database.plugin.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Holiday;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class HolidayDatabaseConnector implements IDatabaseConnector<Holiday> {
	@Override public Holiday create(ResultSet resultSet) throws SQLException {
		return new Holiday(resultSet.getString("name"), resultSet.getDate("date").toLocalDate());
	}

	@Override public PreparedStatement prepareStatement(Holiday holiday, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, holiday.getName());
		preparedStatement.setDate(2, Date.valueOf(holiday.getDate()));
		preparedStatement.setString(3, "holiday");
		return preparedStatement;
	}

	@Override public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.EVENT_DELETE;
			case INSERT:
				return SQLStatements.EVENT_INSERT;
			case SELECT:
				return SQLStatements.HOLIDAY_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}
}
