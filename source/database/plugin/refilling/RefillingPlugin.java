package database.plugin.refilling;

import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.date.Date;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpensePlugin;

public class RefillingPlugin extends InstancePlugin<Refilling> {
	private ExpensePlugin expensePlugin;

	public RefillingPlugin(ExpensePlugin expensePlugin, Storage storage, Backup backup) {
		super("refilling", storage, new RefillingOutputFormatter(), backup);
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(Refilling refilling) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.compareTo(refilling.date) > 0) {
			i--;
		}
		list.add(i, refilling);
	}

	@Override public Refilling create(NamedNodeMap nodeMap) {
		return new Refilling(	Double.valueOf(nodeMap.getNamedItem("distance").getNodeValue()), Double.valueOf(nodeMap.getNamedItem("refuelAmount").getNodeValue()),
								Double.valueOf(nodeMap.getNamedItem("cost").getNodeValue()), new Date(nodeMap.getNamedItem("date").getNodeValue()));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		backup.backup();
		Refilling refilling = new Refilling(Double.valueOf(Terminal.request("distance", "[0-9]{1,13}(\\.[0-9]*)?")),
											Double.valueOf(Terminal.request("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?")),
											Double.valueOf(Terminal.request("cost", "[0-9]{1,13}(\\.[0-9]*)?")), new Date(Terminal.request("date", "DATE")));
		add(refilling);
		expensePlugin.add(new Expense("Auto - Tankstelle", "Fahrtkosten", refilling.cost, refilling.date));
		Terminal.update();
	}
}
