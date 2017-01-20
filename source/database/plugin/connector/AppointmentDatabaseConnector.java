package database.plugin.connector;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import database.plugin.element.Appointment;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.SQLStatements;

public class AppointmentDatabaseConnector implements IDatabaseConnector<Appointment> {
	@Override public Appointment create(ResultSet resultSet) throws SQLException {
		LocalTime beginTime = resultSet.getTime("beginTime") != null ? resultSet.getTime("beginTime").toLocalTime() : null;
		LocalTime endTime = resultSet.getTime("endTime") != null ? resultSet.getTime("endTime").toLocalTime() : null;
		return new Appointment(resultSet.getString("name"), resultSet.getDate("beginDate").toLocalDate(), beginTime, resultSet.getDate("endDate").toLocalDate(), endTime,
			resultSet.getInt("daysTilRepeat"));
	}

	@Override public PreparedStatement deleteQuery(Appointment delete) throws SQLException {
		String sql = SQLStatements.APPOINTMENT_DELETE;
		if (delete.beginTime == null) {
			sql = sql.replace("AND beginTime=?", "AND beginTime is ?");
		}
		if (delete.endTime == null) {
			sql = sql.replace("AND endTime=?", "AND endTime is ?");
		}
		return prepareStatement(delete, sql);
	}

	@Override public PreparedStatement insertQuery(Appointment insert) throws SQLException {
		return prepareStatement(insert, SQLStatements.APPOINTMENT_INSERT);
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.APPOINTMENT_SELECT;
	}

	private PreparedStatement prepareStatement(Appointment appointment, String sql) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		Time beginTime;
		Time endTime;
		if (appointment.beginTime != null) {
			beginTime = Time.valueOf(appointment.beginTime);
		}
		else {
			beginTime = null;
		}
		if (appointment.endTime != null) {
			endTime = Time.valueOf(appointment.endTime);
		}
		else {
			endTime = null;
		}
		preparedStatement.setString(1, appointment.name);
		preparedStatement.setDate(2, Date.valueOf(appointment.beginDate));
		preparedStatement.setTime(3, beginTime);
		preparedStatement.setDate(4, Date.valueOf(appointment.endDate));
		preparedStatement.setTime(5, endTime);
		preparedStatement.setInt(6, appointment.daysTilRepeat);
		return preparedStatement;
	}
}
