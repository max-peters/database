package database.plugin.expense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.expense.monthlyExpense.MonthlyExpensePlugin;

public class ExpensePlugin extends InstancePlugin {
	public ExpensePlugin(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super(pluginContainer, graphicalUserInterface, administration, "expense", new ExpenseList());
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]{0,2})?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void showRequest() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("show", "(all|current|average|month|day)");
		request(map);
		Terminal.printLine(instanceList.output(map));
		graphicalUserInterface.waitForInput();
	}

	@Override public ArrayList<Instance> getList() {
		return instanceList.getList();
	}

	public void initialise() {
		MonthlyExpensePlugin monthlyExpensePlugin = new MonthlyExpensePlugin(pluginContainer, graphicalUserInterface, administration, (ExpenseList) getInstanceList());
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "Miete");
		map.put("category", "Wohnung");
		map.put("value", "350");
		map.put("date", "01.05.2015");
		try {
			monthlyExpensePlugin.create(map);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
