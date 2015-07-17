package database.plugin.refilling;

import database.main.date.Date;
import database.plugin.Instance;

public class Refilling extends Instance {
	public Refilling(String[][] parameter, RefillingList list) {
		super(parameter, parameter[2][1], list);
		setParameter("count", String.valueOf(list.getList().size() + 1));
		createExpense();
	}

	Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	Double getDistance() {
		return Double.valueOf(getParameter("distance"));
	}

	Double getRefuelAmount() {
		return Double.valueOf(getParameter("refuelAmount"));
	}

	Date getDate() {
		return new Date(getParameter("date"));
	}

	int getCount() {
		return Integer.valueOf(getParameter("count"));
	}

	private void createExpense() {
		String[][] parameter = { { "name", "Tankstelle" }, { "category", "Auto" }, { "value", getParameter("value") }, { "date", getParameter("date") } };
		list.add(parameter);
	}

	private double calcAverageConsumption() {
		return Math.round(getRefuelAmount() / getDistance() * 1000) / 10.0;
	}

	protected String output() {
		return "["
				+ String.format("%" + String.valueOf(list.getList().size()).length() + "s", getCount()).replace(' ', '0')
				+ "] distance: ["
				+ String.format("%" + String.valueOf(((RefillingList) list).getHighestDistance()).length() + "s", getDistance())
				+ " km] refuelAmount: ["
				+ String.format("%" + String.valueOf(String.format("%.1f", ((RefillingList) list).getHighestRefuelAmount())).length() + "s", String.format("%.1f", getRefuelAmount()))
						.replace(",", ".") + " l] averageConsumption: ["
				+ String.format("%" + String.valueOf(((RefillingList) list).getHighestAverageConsumption()).length() + "s", calcAverageConsumption()) + " l/km]";
	}
}