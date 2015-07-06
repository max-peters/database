package database.plugin.expense;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Plugin;

public class ExpensePlugin extends Plugin {
	public ExpensePlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "expense");
		this.instanceList = new ExpenseList();
	}

	public String[][] getCreateInformation() {
		String[][] createInformation = { { "name", "[A-ZÖÄÜa-zöäüß\\- ]*" }, { "category", "[A-ZÖÄÜa-zöäüß\\- ]*" }, { "value", "[0-9]{1,13}(\\.[0-9]*)?" }, { "date", null } };
		return createInformation;
	}

	public String[][] getShowInformation() {
		String[][] showInformation = { { "expense", "(all|current|average|month|day)" } };
		return showInformation;
	}

	public String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
