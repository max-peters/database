package database.plugin.calendar.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import database.plugin.calendar.element.Appointment;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class AppointmentDatabaseConnector implements IDatabaseConnector<Appointment> {
	@Override
	public Appointment create(ResultSet resultSet) throws SQLException {
		return new Appointment(resultSet.getString("name"), resultSet.getDate("beginDate").toLocalDate(),
				resultSet.getTime("beginTime").toLocalTime(), resultSet.getDate("endDate").toLocalDate(),
				resultSet.getTime("endTime").toLocalTime(), resultSet.getInt("daysTilRepeat"),
				resultSet.getString("spezification"));
	}

	@Override
	public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.APPOINTMENT_DELETE;
			case INSERT:
				return SQLStatements.APPOINTMENT_INSERT;
			case SELECT:
				return SQLStatements.APPOINTMENT_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override
	public PreparedStatement prepareStatement(Appointment appointment, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, appointment.getName());
		preparedStatement.setDate(2, Date.valueOf(appointment.getDate()));
		preparedStatement.setTime(3, Time.valueOf(appointment.getBeginTime()));
		preparedStatement.setDate(4, Date.valueOf(appointment.getEndDate()));
		preparedStatement.setTime(5, Time.valueOf(appointment.getEndTime()));
		preparedStatement.setInt(6, appointment.getDaysTilRepeat());
		preparedStatement.setString(7, appointment.getSpezification());
		return preparedStatement;
	}
}
