package database.services.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import database.plugin.event.appointment.Appointment;
import database.plugin.event.holiday.Holiday;

public class MySQLDatabase implements IDatabase {
	private static Connection connect = null;

	public MySQLDatabase() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/DATABASE?useSSL=false", "root", "vfr4");
	}

	public ResultSet execute(String sql) throws SQLException {
		return connect.createStatement().executeQuery(sql);
	}

	@Override public void close() throws SQLException {
		connect.close();
	}

	@Override public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connect.prepareStatement(sql);
	}

	static public void add(Holiday b) throws SQLException {
		PreparedStatement s = connect.prepareStatement("INSERT INTO event VALUES (?,?,?);");
		s.setString(1, b.name);
		s.setDate(2, Date.valueOf(b.date));
		s.setString(3, "holiday");
		s.executeUpdate();
		s.close();
	}

	static public void add(Appointment b) throws SQLException {
		Time t1;
		if (b.begin != null) {
			t1 = Time.valueOf(b.begin);
		}
		else {
			t1 = null;
		}
		Time t2;
		if (b.end != null) {
			t2 = Time.valueOf(b.end);
		}
		else {
			t2 = null;
		}
		PreparedStatement s = connect.prepareStatement("INSERT INTO appointment VALUES (?,?,?,?,?,?);");
		s.setString(1, b.name);
		s.setDate(2, Date.valueOf(b.date));
		s.setTime(3, t1);
		s.setDate(4, Date.valueOf(b.date));
		s.setTime(5, t2);
		s.setInt(6, 0);
		s.executeUpdate();
		s.close();
	}
}