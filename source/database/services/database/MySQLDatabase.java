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

	@Override public void close() throws SQLException {
		connect.close();
	}

	@Override public void connect(String url, String user, String password) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection(url, user, password);
		connect.setAutoCommit(true);
	}

	@Override public ResultSet execute(String sql) throws SQLException {
		return connect.createStatement().executeQuery(sql);
	}

	@Override public void insert(Instance instance) throws SQLException {
		executeQuery(instance, QueryType.INSERT);
	}

	@Override public PreparedStatement prepareStatement(String sql) throws SQLException {
		return connect.prepareStatement(sql);
	}

	@Override public void remove(Instance instance) throws SQLException {
		executeQuery(instance, QueryType.DELETE);
	}

	private void executeQuery(Instance instance, QueryType type) throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		IDatabaseConnector<Instance> connector = registry.get((Class<Instance>) instance.getClass());
		PreparedStatement preparedStatement = connector.prepareStatement(instance, connector.getQuery(type));
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}
}