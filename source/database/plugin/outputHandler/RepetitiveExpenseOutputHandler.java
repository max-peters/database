package database.plugin.outputHandler;

import java.sql.SQLException;
import database.plugin.IOutputHandler;

public class RepetitiveExpenseOutputHandler implements IOutputHandler {
	@Override public String getInitialOutput() throws SQLException {
		return "";
	}
}
