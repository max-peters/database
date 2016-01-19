package database.plugin.expense;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.expense.monthlyExpense.MonthlyExpensePlugin;

public class ExpensePlugin extends InstancePlugin {
	public ExpensePlugin(PluginContainer pluginContainer) throws IOException {
		super(pluginContainer, "expense", new ExpenseList());
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException, BadLocationException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("category", "[A-ZÖÄÜa-zöäüß\\- ]+");
		map.put("value", "[0-9]{1,13}(\\.[0-9]{0,2})?");
		map.put("date", null);
		request(map);
		create(map);
		update();
	}

	public void initialise() {
		MonthlyExpensePlugin monthlyExpensePlugin = new MonthlyExpensePlugin(pluginContainer, (ExpenseList) getInstanceList());
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
	// @Command(tag = "show") public void showRequest() throws InterruptedException, BadLocationException {
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("show", "(all|current|average|month|day|category)");
	// request(map);
	// StringBuilder builder = new StringBuilder();
	// for (int i = 0; i < Terminal.getMaximumAmountOfCharactersPerLine('-'); i++) {
	// builder.append("-");
	// }
	// Terminal.printLine(builder.toString(), StringType.REQUEST, StringFormat.STANDARD);
	// Terminal.printLine(instanceList.output(map), StringType.SOLUTION, StringFormat.STANDARD);
	// Terminal.waitForInput();
	// }
}
