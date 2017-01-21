package database.plugin.connector;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Refilling;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class RefillingDatabaseConnector implements IDatabaseConnector<Refilling> {
	@Override public Refilling create(ResultSet resultSet) throws SQLException {
		return new Refilling(resultSet.getDouble("distance"), resultSet.getDouble("refuelAmount"), resultSet.getDouble("cost"), resultSet.getDate("date").toLocalDate());
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.REFILLING_SELECT;
	}

	@Override public PreparedStatement prepareStatement(Refilling refilling, QueryType type) throws SQLException {
		String sql = null;
		PreparedStatement preparedStatement;
		switch (type) {
			case DELETE:
				sql = SQLStatements.REFILLING_DELETE;
				break;
			case INSERT:
				sql = SQLStatements.REFILLING_INSERT;
				break;
			default:
				throw new InvalidParameterException();
		}
		preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setDouble(1, refilling.cost);
		preparedStatement.setDate(2, Date.valueOf(refilling.date));
		preparedStatement.setDouble(3, refilling.distance);
		preparedStatement.setDouble(4, refilling.refuelAmount);
		return preparedStatement;
	}
}
