package database.services.stringComplete;

import java.sql.SQLException;

public interface IStringComplete {
	public String getCorrespondingString(String s) throws SQLException;

	public String getMostUsedString(String prefix, String second) throws SQLException;
}
