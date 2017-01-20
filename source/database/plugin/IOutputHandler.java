package database.plugin;

import java.sql.SQLException;

public interface IOutputHandler {
	String getInitialOutput() throws SQLException;
}
