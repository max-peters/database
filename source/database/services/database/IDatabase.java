package database.services.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDatabase {
	public ResultSet execute(String sql) throws SQLException;

	public void close() throws SQLException;

	public PreparedStatement prepareStatement(String sql) throws SQLException;
}
