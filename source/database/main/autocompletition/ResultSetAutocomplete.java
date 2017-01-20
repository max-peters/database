package database.main.autocompletition;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetAutocomplete implements IAutocomplete {
	private ResultSet resultSet;

	public ResultSetAutocomplete(ResultSet resultSet) throws SQLException {
		this.resultSet = resultSet;
	}

	@Override public String getCorrespondingString(String s) throws SQLException {
		int i = resultSet.getRow();
		resultSet.beforeFirst();
		while (resultSet.next()) {
			if (resultSet.getString("first").equalsIgnoreCase(s)) {
				return resultSet.getString("first");
			}
		}
		resultSet.absolute(i);
		return s;
	}

	@Override public String getMostUsedString(String prefix, String second) throws SQLException {
		String string = "";
		int count = 0;
		resultSet.beforeFirst();
		while (resultSet.next()) {
			if (second.isEmpty() || containsPair(resultSet.getString("first"), second)) {
				if (resultSet.getString("first").toLowerCase().startsWith(prefix.toLowerCase()) && resultSet.getInt("count") > count) {
					string = resultSet.getString("first");
					count = resultSet.getInt("count");
				}
			}
		}
		return second.isEmpty() && prefix.isEmpty() ? "" : string.isEmpty() ? "" : string.substring(prefix.length());
	}

	private boolean containsPair(String first, String second) throws SQLException {
		int i = resultSet.getRow();
		resultSet.beforeFirst();
		while (resultSet.next()) {
			if (resultSet.getString("first").equals(first) && resultSet.getString("second").equals(second)) {
				resultSet.absolute(i);
				return true;
			}
		}
		resultSet.absolute(i);
		return false;
	}
}
