package database.plugin.refilling;

import database.plugin.Instance;

public class Refilling extends Instance {
	public Refilling(String[][] parameter, RefillingList list) {
		super(parameter, parameter[2][1], list);
		setParameter("count", String.valueOf(list.getList().size() + 1));
		createExpense();
	}

	@Override public String[][] getParameter() {
		return new String[][] { { "refuelAmount", getParameter("refuelAmount") }, { "value", getParameter("value") }, { "distance", getParameter("distance") }, { "date", getParameter("date") },
				{ "count", getParameter("count") } };
	}

	private void createExpense() {
		String[][] parameter = { { "name", "Tankstelle" }, { "category", "Auto" }, { "value", getParameter("value") }, { "date", getParameter("date") } };
		list.add(parameter);
	}

	private double calcAverageConsumption() {
		return Math.round(Double.valueOf(getParameter("refuelAmount")) / Double.valueOf(getParameter("distance")) * 1000) / 10.0;
	}

	protected String output() {
		return "["
				+ String.format("%" + String.valueOf(list.getList().size()).length() + "s", Integer.valueOf(getParameter("count"))).replace(' ', '0')
				+ "] distance: ["
				+ String.format("%" + String.valueOf(((RefillingList) list).getHighestDistance()).length() + "s", Double.valueOf(getParameter("distance")))
				+ " km] refuelAmount: ["
				+ String.format("%" + String.valueOf(String.format("%.1f", ((RefillingList) list).getHighestRefuelAmount())).length() + "s",
						String.format("%.1f", Double.valueOf(getParameter("refuelAmount")))).replace(",", ".") + " l] averageConsumption: ["
				+ String.format("%" + String.valueOf(((RefillingList) list).getHighestAverageConsumption()).length() + "s", calcAverageConsumption()) + " l/km]";
	}
}