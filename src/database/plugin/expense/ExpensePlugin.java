package database.plugin.expense;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;

public class ExpensePlugin extends Plugin {
	public ExpensePlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "expense");
		this.instanceList = new ExpenseList();
	}

	protected String[][] getCreateInformation() {
		String[][] createInformation = { { "name", "[A-ZÖÄÜa-zöäüß\\- ]*" }, { "category", "[A-ZÖÄÜa-zöäüß\\- ]*" }, { "value", "[0-9]{1,13}(\\.[0-9]*)?" }, { "date", null } };
		return createInformation;
	}

	protected String[][] getShowInformation() {
		String[][] showInformation = { { "expense", "(all|current|average|month|day)" } };
		return showInformation;
	}

	protected String[][] getChangeInformation() {
		return null;
	}

	protected void change(String[] parameter) {
	}
}
