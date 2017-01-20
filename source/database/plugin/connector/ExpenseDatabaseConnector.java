package database.plugin.connector;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.element.Expense;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.IDatabaseConnector;
import database.services.database.SQLStatements;

public class ExpenseDatabaseConnector implements IDatabaseConnector<Expense> {
	public boolean contains(Expense expense) throws SQLException {
		PreparedStatement preparedStatement = prepareStatement(expense, SQLStatements.EXPENSE_SELECT_CONTAINS);
		ResultSet resultSet = preparedStatement.executeQuery();
		boolean isContained = resultSet.first();
		preparedStatement.close();
		resultSet.close();
		return isContained;
	}

	@Override public Expense create(ResultSet resultSet) throws SQLException {
		return new Expense(resultSet.getString("name"), resultSet.getString("category"), resultSet.getDouble("value"), resultSet.getDate("date").toLocalDate());
	}

	@Override public PreparedStatement deleteQuery(Expense delete) throws SQLException {
		return prepareStatement(delete, SQLStatements.EXPENSE_DELETE);
	}

	@Override public PreparedStatement insertQuery(Expense insert) throws SQLException {
		return prepareStatement(insert, SQLStatements.EXPENSE_INSERT);
	}

	@Override public String selectQuery() throws SQLException {
		return SQLStatements.EXPENSE_SELECT;
	}

	private PreparedStatement prepareStatement(Expense expense, String sql) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(sql);
		preparedStatement.setString(1, expense.name);
		preparedStatement.setString(2, expense.category);
		preparedStatement.setDate(3, Date.valueOf(expense.date));
		preparedStatement.setDouble(4, expense.value);
		return preparedStatement;
	}
}
