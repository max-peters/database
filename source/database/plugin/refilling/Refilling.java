package database.plugin.refilling;

import java.util.HashMap;
import java.util.Map;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.expense.ExpenseList;

public class Refilling extends Instance {
	public Refilling(Map<String, String> parameter, RefillingList list) {
		super(parameter, "refilling", list);
		setParameter("count", String.valueOf(list.getList().size() + 1));
	}

	@Getter Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	@Getter Double getDistance() {
		return Double.valueOf(getParameter("distance"));
	}

	@Getter Double getRefuelAmount() {
		return Double.valueOf(getParameter("refuelAmount"));
	}

	@Getter Date getDate() {
		return new Date(getParameter("date"));
	}

	@Getter int getCount() {
		return Integer.valueOf(getParameter("count"));
	}

	public void createExpense(ExpenseList expenseList) {
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