package database.plugin.refilling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstanceList;
import database.plugin.expense.ExpensePlugin;

public class RefillingList extends InstanceList {
	private ExpensePlugin expensePlugin;

	public RefillingList(ExpensePlugin expensePlugin) {
		super();
		this.expensePlugin = expensePlugin;
	}

	@Override public void add(Map<String, String> parameter) throws IOException {
		Refilling refilling = new Refilling(parameter);
		list.add(refilling);
		createExpense(refilling, expensePlugin);
	}

	@Override public String initialOutput() {
		String output = "";
		if (isEmpty()) {
			output = "no entries";
		}
		else {
			output = "["+ list.size() + "] " + "distance: " + "[" + getDistanceTotal() + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal() + " l" + "] "
						+ "averageConsumption: " + "[" + getAverageConsumptionTotal() + " l/km" + "]";
		}
		return output;
	}

	@Command(tag = "all") public String outputAll() {
		StringBuilder builder = new StringBuilder();
		if (isEmpty()) {
			builder.append("no entries");
		}
		else {
			for (Instance instance : getIterable()) {
				Refilling refilling = (Refilling) instance;
				builder.append("[" + String.format("%" + String.valueOf(list.size()).length() + "s", list.indexOf(instance)).replace(' ', '0'));
				builder.append("] distance: [" + String.format("%" + String.valueOf(getHighestDistance()).length() + "s", refilling.getDistance()) + " km] refuelAmount: [");
				builder.append(String.format("%"+ String.valueOf(String.format("%.1f", getHighestRefuelAmount())).length() + "s",
												String.format("%.1f", refilling.getRefuelAmount()))
										.replace(",", "."));
				builder.append(" l] averageConsumption: ["+ String.format("%" + String.valueOf(getHighestAverageConsumption()).length() + "s", refilling.calcAverageConsumption())
								+ " l/km]" + System.getProperty("line.separator"));
			}
		}
		return builder.toString();
	}

	protected double getHighestAverageConsumption() {
		double highestConsumption = 0;
		double currentConsumption;
		for (Instance instance : getIterable()) {
			Refilling refilling = (Refilling) instance;
			currentConsumption = Math.round(refilling.getRefuelAmount() / refilling.getDistance() * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}

	protected double getHighestDistance() {
		double highestDistance = 0;
		for (Instance instance : getIterable()) {
			Refilling refilling = (Refilling) instance;
			if (refilling.getDistance() > highestDistance) {
				highestDistance = refilling.getDistance();
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount() {
		double highestRefuelAmount = 0;
		for (Instance instance : getIterable()) {
			Refilling refilling = (Refilling) instance;
			if (refilling.getRefuelAmount() > highestRefuelAmount) {
				highestRefuelAmount = refilling.getRefuelAmount();
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	private double getAverageConsumptionTotal() {
		return Math.round(getRefuelAmountTotal() / getDistanceTotal() * 1000.0) / 10.0;
	}

	private double getDistanceTotal() {
		double distanceTotal = 0;
		for (Instance instance : getIterable()) {
			Refilling refilling = (Refilling) instance;
			distanceTotal = distanceTotal + refilling.getDistance();
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal() {
		double refuelAmountTotal = 0;
		for (Instance instance : getIterable()) {
			Refilling refilling = (Refilling) instance;
			refuelAmountTotal = refuelAmountTotal + refilling.getRefuelAmount();
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}

	private void createExpense(Refilling refilling, ExpensePlugin expensePlugin) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "Auto - Tankstelle");
		map.put("category", "Fahrtkosten");
		map.put("value", refilling.getParameter("value"));
		map.put("date", refilling.getParameter("date"));
		expensePlugin.create(map);
	}
}