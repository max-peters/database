package database.plugin.refilling;

import java.awt.print.PrinterAbortException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;

public class RefillingPlugin extends Plugin {
	public RefillingPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "refilling");
		this.instanceList = new RefillingList();
	}

	public String[][] getCreateInformation() {
		String[][] createInformation = { { "refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?" }, { "value", "[0-9]{1,13}(\\.[0-9]*)?" }, { "distance", "[0-9]{1,13}(\\.[0-9]*)?" }, { "date", null } };
		return createInformation;
	}

	public String[][] getShowInformation() throws PrinterAbortException {
		throw new PrinterAbortException();
	}

	public String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
