package database.plugin.subject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;

public class SubjectPlugin extends InstancePlugin {
	public SubjectPlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "subject", new SubjectList());
	}

	@Command(tag = "add") public void addRequest() throws InterruptedException, BadLocationException {
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

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException, BadLocationException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ].*");
		map.put("tag", "[a-zöäüß]*");
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void showRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("show", "(average|" + ((SubjectList) instanceList).getTagsAsRegex() + ")");
		request(map);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < Terminal.getMaximumAmountOfCharactersPerLine('-'); i++) {
			builder.append("-");
		}
		Terminal.printLine(builder.toString(), StringType.REQUEST, StringFormat.STANDARD);
		Terminal.printLine(instanceList.output(map), StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}
}
