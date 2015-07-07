package database.plugin.subject;

import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Plugin;

public class SubjectPlugin extends Plugin {
	public SubjectPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "subject", new SubjectList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			create(request(new String[][] { { "name", "[A-ZÖÄÜ].*" }, { "tag", "[a-zöäüß]*" } }));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Command(tag = "show") public void show() throws InterruptedException, NotImplementedException {
		try {
			terminal.solutionOut(instanceList.output(request(new String[][] { { "show", "(average|" + ((SubjectList) instanceList).getTagsAsRegex() + ")" } })));
			graphicalUserInterface.waitForInput();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Command(tag = "add") public void change() throws InterruptedException, NotImplementedException {
		try {
			change(request(new String[][] { { "add", ((SubjectList) instanceList).getTagsAsRegex() }, { "grade", "[0-9]{1,13}(\\.[0-9]*)?" } }));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "add":
				change();
				break;
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

	private void change(String[] parameter) {
		Subject toChange = ((SubjectList) instanceList).getSubject(parameter[0]);
		toChange.setGrade(Double.parseDouble(parameter[1]), Double.parseDouble(parameter[2]));
	}
}
