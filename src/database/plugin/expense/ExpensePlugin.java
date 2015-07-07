package database.plugin.expense;

import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Plugin;

public class ExpensePlugin extends Plugin {
	public ExpensePlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "expense", new ExpenseList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			create(request(new String[][] { { "name", "[A-ZÖÄÜa-zöäüß\\- ]*" }, { "category", "[A-ZÖÄÜa-zöäüß\\- ]*" }, { "value", "[0-9]{1,13}(\\.[0-9]*)?" }, { "date", null } }));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Command(tag = "show") public void show() throws InterruptedException, NotImplementedException {
		try {
			terminal.solutionOut(instanceList.output(request(new String[][] { { "show", "(all|current|average|month|day)" } })));
			graphicalUserInterface.waitForInput();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "show":
				show();
				break;
			case "store":
				store();
				break;
			case "display":
				display();
				break;
		}
	}
}
