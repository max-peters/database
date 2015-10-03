package database.plugin.refilling;

import java.util.HashMap;
import java.util.Map;

import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.expense.ExpenseList;

public class Refilling extends Instance {
	public Refilling(Map<String, String> parameter, RefillingList list, ExpenseList expenseList) {
		super(parameter, list);
		setParameter("count", String.valueOf(list.getList().size() + 1));
		assert parameter.size() == 5;
		assert parameter.containsKey("value");
		assert parameter.containsKey("distance");
		assert parameter.containsKey("refuelAmount");
		assert parameter.containsKey("date");
		assert parameter.containsKey("count");
		createExpense(expenseList);
	}

	protected Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	protected Double getDistance() {
		return Double.valueOf(getParameter("distance"));
	}

	protected Double getRefuelAmount() {
		return Double.valueOf(getParameter("refuelAmount"));
	}

	protected Date getDate() {
		return new Date(getParameter("date"));
	}

	private int getCount() {
		return Integer.valueOf(getParameter("count"));
	}

	private void createExpense(ExpenseList expenseList) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "Tankstelle - Auto");
		map.put("category", "Transport");
		map.put("value", getParameter("value"));
		map.put("date", getParameter("date"));
		expenseList.add(map);
	}

	private double calcAverageConsumption() {
		return Math.round(getRefuelAmount() / getDistance() * 1000) / 10.0;
	}

	protected String output() {
		return "["
				+ String.format("%" + String.valueOf(getInstanceList().getList().size()).length() + "s", getCount()).replace(' ', '0')
				+ "] distance: ["
				+ String.format("%" + String.valueOf(((RefillingList) getInstanceList()).getHighestDistance()).length() + "s", getDistance())
				+ " km] refuelAmount: ["
				+ String.format("%" + String.valueOf(String.format("%.1f", ((RefillingList) getInstanceList()).getHighestRefuelAmount())).length() + "s", String.format("%.1f", getRefuelAmount()))
						.replace(",", ".") + " l] averageConsumption: ["
				+ String.format("%" + String.valueOf(((RefillingList) getInstanceList()).getHighestAverageConsumption()).length() + "s", calcAverageConsumption()) + " l/km]";
	}
}