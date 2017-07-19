package database.plugin.calendar.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.plugin.calendar.element.Day;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class DayDatabaseConnector implements IDatabaseConnector<Day> {
	@Override
	public Day create(ResultSet resultSet) throws SQLException {
		return new Day(resultSet.getString("name"), resultSet.getDate("date").toLocalDate());
	}

	@Override
	public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.EVENT_DELETE;
			case INSERT:
				return SQLStatements.EVENT_INSERT;
			case SELECT:
				return SQLStatements.DAY_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override
	public PreparedStatement prepareStatement(Day day, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, day.getName());
		preparedStatement.setDate(2, Date.valueOf(day.getDate()));
		preparedStatement.setString(3, "day");
		return preparedStatement;
	}
}
