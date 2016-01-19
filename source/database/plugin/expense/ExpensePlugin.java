package database.plugin.expense;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.RequestInformation;
import database.plugin.expense.monthlyExpense.MonthlyExpensePlugin;

public class ExpensePlugin extends InstancePlugin {
	private MonthlyExpensePlugin monthlyExpensePlugin;

	public ExpensePlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "expense", new ExpenseList());
		monthlyExpensePlugin = new MonthlyExpensePlugin(pluginContainer, (ExpenseList) getInstanceList());
	}

	@Command(tag = "bla") public void bla() {
		for (Instance tt : monthlyExpensePlugin.getList()) {
			for (Entry<String, String> ii : tt.getParameter().entrySet()) {
				System.out.println(ii.getKey() + " - " + ii.getValue());
			}
		}
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]{0,2})?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	@Override public List<RequestInformation> getInformationList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		for (int i = 0; i < getList().size(); i++) {
			list.add(new RequestInformation("expense", getList().get(i).getParameter()));
		}
		for (int i = 0; i < monthlyExpensePlugin.getList().size(); i++) {
			list.add(new RequestInformation("monthlyexpense", monthlyExpensePlugin.getList().get(i).getParameter()));
		}
		list.add(new RequestInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void readInformation(RequestInformation pair) throws IOException {
		if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else if (pair.getName().equals("expense")) {
			create(pair.getMap());
		}
		else if (pair.getName().equals("monthlyexpense")) {
			monthlyExpensePlugin.create(pair.getMap());
		}
	}
}
