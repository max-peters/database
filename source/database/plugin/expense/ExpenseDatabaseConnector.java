package database.plugin.expense;

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
import database.services.stringComplete.IStringComplete;
import database.services.stringComplete.ResultSetStringComplete;

public class ExpenseDatabaseConnector implements IDatabaseConnector<Expense> {
	public IStringComplete	categoryStringComplete;
	public IStringComplete	nameStringComplete;

	public boolean contains(Expense expense) throws SQLException {
		PreparedStatement preparedStatement = prepareStatement(expense, getQuery(QueryType.CONTAINS));
		ResultSet resultSet = preparedStatement.executeQuery();
		boolean isContained = resultSet.first();
		preparedStatement.close();
		resultSet.close();
		return isContained;
	}

	@Override public Expense create(ResultSet resultSet) throws SQLException {
		return new Expense(resultSet.getString("name"), resultSet.getString("category"), resultSet.getDouble("value"), resultSet.getDate("date").toLocalDate());
	}

	@Override public String getQuery(QueryType type) throws SQLException {
		switch (type) {
			case DELETE:
				return SQLStatements.EXPENSE_DELETE;
			case INSERT:
				return SQLStatements.EXPENSE_INSERT;
			case SELECT:
				return SQLStatements.EXPENSE_SELECT;
			case CONTAINS:
				return SQLStatements.EXPENSE_SELECT_CONTAINS;
			default:
				throw new InvalidParameterException();
		}
	}

	@Override public PreparedStatement prepareStatement(Expense expense, String query) throws SQLException {
		PreparedStatement preparedStatement = ServiceRegistry.Instance().get(IDatabase.class).prepareStatement(query);
		preparedStatement.setString(1, expense.getName());
		preparedStatement.setString(2, expense.getCategory());
		preparedStatement.setDate(3, Date.valueOf(expense.getDate()));
		preparedStatement.setDouble(4, expense.getValue());
		return preparedStatement;
	}

	public void refreshStringComplete() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		if (nameStringComplete != null && categoryStringComplete != null) {
			((ResultSetStringComplete) nameStringComplete).refresh(database.execute(SQLStatements.EXPENSE_SELECT_STRINGCOMPLETE_NAME));
			((ResultSetStringComplete) categoryStringComplete).refresh(database.execute(SQLStatements.EXPENSE_SELECT_STRINGCOMPLETE_CATEGORY));
		}
		else {
			nameStringComplete = new ResultSetStringComplete(database.execute(SQLStatements.EXPENSE_SELECT_STRINGCOMPLETE_NAME));
			categoryStringComplete = new ResultSetStringComplete(database.execute(SQLStatements.EXPENSE_SELECT_STRINGCOMPLETE_CATEGORY));
		}
	}
}
