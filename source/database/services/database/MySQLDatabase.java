package database.services.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.Instance;
import database.services.ServiceRegistry;

public class MySQLDatabase implements IDatabase {
	private Connection connect = null;

	public MySQLDatabase() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/DATABASE?useSSL=false", "root", "vfr4");
		connect.setAutoCommit(true);
	}

	@Override public void close() throws SQLException {
		connect.close();
	}

	@Override public ResultSet execute(String sql) throws SQLException {
		return connect.createStatement().executeQuery(sql);
	}

	@Override public void insert(Instance instance) throws SQLException {
		ConnectorRegistry registry = ServiceRegistry.Instance().get(ConnectorRegistry.class);
		PreparedStatement preparedStatement = registry.get((Class<Instance>) instance.getClass()).insertQuery(instance);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

	@Override public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connect.prepareStatement(sql);
	}

	@Override public void remove(Instance instance) throws SQLException {
		ConnectorRegistry registry = ServiceRegistry.Instance().get(ConnectorRegistry.class);
		PreparedStatement preparedStatement = registry.get((Class<Instance>) instance.getClass()).deleteQuery(instance);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}
}