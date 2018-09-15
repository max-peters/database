package database.plugin.stromWasser;

import java.sql.SQLException;

import database.plugin.IOutputHandler;

public class StromWasserOutputHandler implements IOutputHandler {
	@Override
	public String getInitialOutput() throws SQLException {
		return "";
	}
}
