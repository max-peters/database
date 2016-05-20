package database.plugin.refilling;

import java.time.LocalDate;
import database.plugin.Instance;

public class Refilling extends Instance {
	public Double		cost;
	public LocalDate	date;
	public Double		distance;
	public Double		refuelAmount;

	public Refilling(Double distance, Double refuelAmount, Double cost, LocalDate date) {
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
			if (date.isEqual(refilling.date) && distance.equals(refilling.distance) && cost.equals(refilling.cost) && refuelAmount.equals(refilling.refuelAmount)) {
				return true;
			}
		}
		return false;
	}
}