package database.plugin.refilling;

import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;

public class Refilling extends Instance {
	public Refilling(Map<String, String> parameter) {
		super(parameter);
		parameter.replace("value", String.valueOf(Double.valueOf(getParameter("value"))));
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
}