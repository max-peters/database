package database.services.stringComplete;

import java.sql.SQLException;

public interface IFrequentStringComplete {
	public void create(String type) throws SQLException;

	public IStringComplete get(String type);

	public void insert(String string, String type) throws SQLException;
}
