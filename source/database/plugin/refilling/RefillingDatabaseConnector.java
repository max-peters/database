package database.plugin.refilling;

import java.security.InvalidParameterException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.QueryType;
import database.services.database.SQLStatements;

public class RefillingDatabaseConnector implements IDatabaseConnector<Refilling> {
	@Override public Refilling create(ResultSet resultSet) throws SQLException {
		return new Refilling(resultSet.getDouble("distance"), resultSet.getDouble("refuelAmount"), resultSet.getDouble("cost"), resultSet.getDate("date").toLocalDate());
	}

	@Override public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.REFILLING_DELETE;
			case INSERT:
				return SQLStatements.REFILLING_INSERT;
			case SELECT:
				return SQLStatements.REFILLING_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override public PreparedStatement prepareStatement(Refilling refilling, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setDouble(1, refilling.getCost());
		preparedStatement.setDate(2, Date.valueOf(refilling.getDate()));
		preparedStatement.setDouble(3, refilling.getDistance());
		preparedStatement.setDouble(4, refilling.getRefuelAmount());
		return preparedStatement;
	}
}
