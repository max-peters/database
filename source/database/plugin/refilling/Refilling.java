package database.plugin.refilling;

import java.util.HashMap;
import java.util.Map;
import database.main.date.Date;
import database.plugin.Instance;

public class Refilling extends Instance {
	public Double	cost;
	public Date		date;
	public Double	distance;
	public Double	refuelAmount;

	public Refilling(Map<String, String> parameter) {
		date = new Date(parameter.get("date"));
		distance = Double.valueOf(parameter.get("distance"));
		refuelAmount = Double.valueOf(parameter.get("refuelAmount"));
		cost = Double.valueOf(parameter.get("cost"));
	}

	public double calcAverageConsumption() {
		return Math.round(refuelAmount / distance * 1000) / 10.0;
	}

	@Override public boolean equals(Object object) {
		Refilling refilling;
		if (object != null && object.getClass().equals(this.getClass())) {
			refilling = (Refilling) object;
			if (date.equals(refilling.date) && distance.equals(refilling.distance) && cost.equals(refilling.cost) && refuelAmount.equals(refilling.refuelAmount)) {
				return true;
			}
		}
		return false;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("distance", distance.toString());
		parameter.put("refuelAmount", refuelAmount.toString());
		parameter.put("cost", cost.toString());
		parameter.put("date", date.toString());
		return parameter;
	}
}