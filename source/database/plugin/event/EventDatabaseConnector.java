package database.plugin.event;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.plugin.event.birthday.Birthday;
import database.services.database.IDatabase;

public class EventDatabaseConnector {
	private IDatabase database;

	public EventDatabaseConnector(IDatabase database) {
		this.database = database;
	}

	public void addBirthday(Birthday b) throws SQLException {
		PreparedStatement s = database.prepareStatement("INSERT INTO event VALUES (?,?,?);");
		s.setString(1, b.name);
		s.setDate(2, java.sql.Date.valueOf(b.date));
		s.setString(3, "birthday");
	}
}
