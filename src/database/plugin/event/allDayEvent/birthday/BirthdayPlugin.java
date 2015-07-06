package database.plugin.event.allDayEvent.birthday;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Plugin;

public class BirthdayPlugin extends Plugin {
	public BirthdayPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "birthday");
		this.instanceList = new BirthdayList();
		this.display = true;
	}

	public String[][] getCreateInformation() throws NotImplementedException {
		String[][] createInformation = { { "name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)" }, { "date", null } };
		return createInformation;
	}

	public String[][] getShowInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}

	public String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}