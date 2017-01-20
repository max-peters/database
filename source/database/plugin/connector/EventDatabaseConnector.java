package database.plugin.connector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import database.plugin.element.Event;
import database.services.database.IDatabaseConnector;
import database.services.database.SQLStatements;

public abstract class EventDatabaseConnector<E extends Event> implements IDatabaseConnector<E> {
	@Override public PreparedStatement deleteQuery(E delete) throws SQLException {
		return prepareStatement(delete, SQLStatements.EVENT_DELETE);
	}

	@Override public PreparedStatement insertQuery(E insert) throws SQLException {
		return prepareStatement(insert, SQLStatements.EVENT_INSERT);
	}

	protected abstract PreparedStatement prepareStatement(E event, String sql) throws SQLException;
}
