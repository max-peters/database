package database.services.database;

import java.sql.SQLException;

public interface IConnectorRegistry {
	public <I> IDatabaseConnector<I> get(Class<I> type) throws SQLException;

	public <I> void register(Class<I> type, IDatabaseConnector<I> connector);
}
