package database.plugin.event.birthday;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
		String[][] createInformation = { { "name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)" }, { "date", null } };
		return createInformation;
	}

	protected String[][] getShowInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}

	protected String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
