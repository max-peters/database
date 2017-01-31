package database.plugin.expense.repetitive;

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

public class RepetitiveExpenseDatabaseConnector implements IDatabaseConnector<RepetitiveExpense> {
	@Override public RepetitiveExpense create(ResultSet resultSet) throws SQLException {
		return new RepetitiveExpense(resultSet.getString("name"), resultSet.getString("category"), resultSet.getDouble("value"), resultSet.getDate("date").toLocalDate(),
			ExecutionDay.getExecutionDay(resultSet.getString("executionDay")), resultSet.getInt("repeatInterval"));
	}

	@Override public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.REPETITITVEEXPENSE_DELETE;
			case INSERT:
				return SQLStatements.REPETITITVEEXPENSE_INSERT;
			case SELECT:
				return SQLStatements.REPETITITVEEXPENSE_SELECT;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override public PreparedStatement prepareStatement(RepetitiveExpense repetitiveExpense, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, repetitiveExpense.getName());
		preparedStatement.setString(2, repetitiveExpense.getCategory());
		preparedStatement.setDouble(3, repetitiveExpense.getValue());
		preparedStatement.setDate(4, Date.valueOf(repetitiveExpense.getDate()));
		preparedStatement.setString(5, repetitiveExpense.getExecutionDay().toString());
		preparedStatement.setInt(6, repetitiveExpense.getInterval());
		return preparedStatement;
	}
}
