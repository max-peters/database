package database.plugin.subject;

import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class SubjectPlugin extends InstancePlugin {
	public SubjectPlugin(PluginContainer store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "subject", new SubjectList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			String[][] parameter = request(new String[][] { { "name", "[A-ZÖÄÜ].*" }, { "tag", "[a-zöäüß]*" } });
			create(new String[][] { { parameter[0][0], parameter[0][1] }, { parameter[1][0], parameter[1][1] }, { "score", "0" }, { "maxPoints", "0" }, { "counter", "0" } });
			update();
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
			change(request(new String[][] { { "add", ((SubjectList) instanceList).getTagsAsRegex() }, { "score", "[0-9]{1,13}(\\.[0-9]*)?" }, { "maximum points", "[0-9]{1,13}(\\.[0-9]*)?" } }));
			update();
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
			case "display":
				display();
				break;
		}
	}

	private void change(String[][] parameter) {
		Subject toChange = ((SubjectList) instanceList).getSubject(parameter[0][1]);
		toChange.setGrade(Double.parseDouble(parameter[1][1]), Double.parseDouble(parameter[2][1]));
		toChange.calcPercent();
	}
}
