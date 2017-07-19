package database.services.stringComplete;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.SQLStatements;

public class ResultSetFrequentStringComplete implements IFrequentStringComplete {
	private Map<String, IStringComplete> map;

	public ResultSetFrequentStringComplete() {
		map = new HashMap<>();
	}

	@Override
	public void create(String type) throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		PreparedStatement preparedStatement = database
				.prepareStatement(SQLStatements.FREQUENTNAMES_SELECT_STRINGCOMPLETE);
		preparedStatement.setString(1, type);
		map.put(type, new ResultSetStringComplete(preparedStatement.executeQuery()));
	}

	@Override
	public IStringComplete get(String type) {
		return map.get(type);
	}

	@Override
	public void insert(String string, String type) throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		PreparedStatement preparedStatement = database.prepareStatement(SQLStatements.FREQUENTNAMES_INSERT);
		preparedStatement.setString(1, string);
		preparedStatement.setString(2, type);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		preparedStatement = database.prepareStatement(SQLStatements.FREQUENTNAMES_SELECT_STRINGCOMPLETE);
		preparedStatement.setString(1, type);
		((ResultSetStringComplete) map.get(type)).refresh(preparedStatement.executeQuery());
	}
}
