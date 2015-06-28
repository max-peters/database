package database.plugin.birthday;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;

public class BirthdayPlugin extends Plugin {
	public BirthdayPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "birthday");
		this.instanceList = new BirthdayList();
	}

	protected String[][] getCreateInformation() {
		String[][] createInformation = { { "name", "[[A-ZÖÄÜ][a-zöäüß]* -]*" }, { "date", null } };
		return createInformation;
	}

	protected String[][] getShowInformation() {
		return null;
	}

	protected String[][] getChangeInformation() {
		return null;
	}

	protected String show(String[] parameter) {
		return null;
	}

	protected void change(String[] parameter) {
	}
}
