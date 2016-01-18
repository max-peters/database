package database.plugin.refilling;

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
import database.plugin.expense.ExpenseList;

public class RefillingPlugin extends InstancePlugin {
	public RefillingPlugin(PluginContainer pluginContainer, ExpenseList expenseList) {
		super(pluginContainer, "refilling", new RefillingList(expenseList));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException, BadLocationException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("distance", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void showRequest() throws InterruptedException, BadLocationException {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < Terminal.getMaximumAmountOfCharactersPerLine('-'); i++) {
			builder.append("-");
		}
		Terminal.printLine(builder.toString(), StringType.REQUEST, StringFormat.STANDARD);
		Terminal.printLine(instanceList.output(null), StringType.SOLUTION, StringFormat.STANDARD);
		Terminal.waitForInput();
	}
}
