package database.plugin.connector;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Day;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.SQLStatements;

public class DayDatabaseConnector extends EventDatabaseConnector<Day> {
	@Override public Day create(ResultSet resultSet) throws SQLException {
		return new Day(resultSet.getString("name"), resultSet.getDate("date").toLocalDate());
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.DAY_SELECT;
	}

	@Override protected PreparedStatement prepareStatement(Day day, String sql) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, day.getName());
		preparedStatement.setDate(2, Date.valueOf(day.getDate()));
		preparedStatement.setString(3, "day");
		return preparedStatement;
	}
}
