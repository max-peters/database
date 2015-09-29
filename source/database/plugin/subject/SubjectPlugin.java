package database.plugin.subject;

import java.util.HashMap;
import java.util.Map;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class SubjectPlugin extends InstancePlugin {
	public SubjectPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "subject", new SubjectList());
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ].*");
		map.put("tag", "[a-zöäüß]*");
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void show() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("show", "(average|" + ((SubjectList) instanceList).getTagsAsRegex() + ")");
		request(map);
		terminal.solutionOut(instanceList.output(map));
		graphicalUserInterface.waitForInput();
	}

	@Command(tag = "add") public void add() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("add", ((SubjectList) instanceList).getTagsAsRegex());
		map.put("score", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("maximum points", "[0-9]{1,13}(\\.[0-9]*)?");
		request(map);
		Subject toChange = ((SubjectList) instanceList).getSubject(map.get("add"));
		toChange.setGrade(Double.parseDouble(map.get("score")), Double.parseDouble(map.get("maximum points")));
		toChange.calcPercent();
		update();
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "add":
				add();
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
}
