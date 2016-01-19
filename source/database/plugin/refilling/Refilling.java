package database.plugin.refilling;

import java.util.HashMap;
import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.expense.ExpenseList;

public class Refilling extends Instance {
	public Refilling(Map<String, String> parameter, ExpenseList expenseList) {
		super(parameter);
		parameter.replace("value", String.valueOf(Double.valueOf(getParameter("value"))));
		createExpense(expenseList);
	}

	public double calcAverageConsumption() {
		return Math.round(getRefuelAmount() / getDistance() * 1000) / 10.0;
	}

	protected Date getDate() {
		return new Date(getParameter("date"));
	}

	protected Double getDistance() {
		return Double.valueOf(getParameter("distance"));
	}

	protected Double getRefuelAmount() {
		return Double.valueOf(getParameter("refuelAmount"));
	}

	protected Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	private void createExpense(ExpenseList expenseList) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "Auto - Tankstelle");
		map.put("category", "Fahrtkosten");
		map.put("value", getParameter("value"));
		map.put("date", getParameter("date"));
		expenseList.add(map);
	}
}