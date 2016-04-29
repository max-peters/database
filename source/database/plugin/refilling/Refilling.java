package database.plugin.refilling;

import org.w3c.dom.Element;
import database.main.date.Date;
import database.plugin.Instance;

public class Refilling extends Instance {
	public Double	cost;
	public Date		date;
	public Double	distance;
	public Double	refuelAmount;

	public Refilling(Double distance, Double refuelAmount, Double cost, Date date) {
		this.distance = distance;
		this.refuelAmount = refuelAmount;
		this.cost = cost;
		this.date = date;
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

	@Override public void insertParameter(Element element) {
		element.setAttribute("distance", distance.toString());
		element.setAttribute("refuelAmount", refuelAmount.toString());
		element.setAttribute("cost", cost.toString());
		element.setAttribute("date", date.toString());
	}
}