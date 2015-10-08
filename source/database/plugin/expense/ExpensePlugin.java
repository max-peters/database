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
	private ArrayList<InstancePlugin>	extentionList;

	public ExpensePlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "expense", new ExpenseList());
		extentionList = new ArrayList<InstancePlugin>();
		initialise();
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	@Command(tag = "show") public void showRequest() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("show", "(all|current|average|month|day)");
		request(map);
		terminal.solutionOut(instanceList.output(map));
		graphicalUserInterface.waitForInput();
	}

	@Override public ArrayList<Instance> getList() {
		return instanceList.getList();
	}

	private void initialise() {
		extentionList.add(new MonthlyExpensePlugin(pluginContainer, terminal, graphicalUserInterface, administration, (ExpenseList) getInstanceList()));
	}
}
