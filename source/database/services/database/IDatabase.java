package database.services.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.Instance;

public interface IDatabase {
	public void close() throws SQLException;

	public ResultSet execute(String sql) throws SQLException;

	public void insert(Instance instance) throws SQLException;

	public PreparedStatement prepareStatement(String sql) throws SQLException;

	public void remove(Instance instance) throws SQLException;
}
