package database.plugin.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Day;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class DayDatabaseConnector implements IDatabaseConnector<Day> {
	@Override public Day create(ResultSet resultSet) throws SQLException {
		return new Day(resultSet.getString("name"), resultSet.getDate("date").toLocalDate());
	}

	@Override public PreparedStatement prepareStatement(Day day, QueryType type) throws SQLException {
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
		preparedStatement.setString(1, day.getName());
		preparedStatement.setDate(2, Date.valueOf(day.getDate()));
		preparedStatement.setString(3, "day");
		return preparedStatement;
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.DAY_SELECT;
	}
}
