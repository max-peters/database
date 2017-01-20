package database.main.autocompletition;

import java.sql.SQLException;

public interface IAutocomplete {
	public String getCorrespondingString(String s) throws SQLException;

	public String getMostUsedString(String prefix, String second) throws SQLException;
}
