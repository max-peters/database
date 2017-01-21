package database.services.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import database.services.ServiceRegistry;

public interface IDatabaseConnector<I> {
	public I create(ResultSet resultSet) throws SQLException;

	public default List<I> getList() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		List<I> list = new LinkedList<>();
		ResultSet resultSet = database.execute(selectQuery());
		while (resultSet.next()) {
			list.add(create(resultSet));
		}
		return list;
	}

	public PreparedStatement prepareStatement(I element, QueryType type) throws SQLException;

	public String selectQuery() throws SQLException;
}
