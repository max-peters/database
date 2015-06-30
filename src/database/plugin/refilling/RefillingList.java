package database.plugin.refilling;

import database.main.Instance;
import database.main.InstanceList;

public class RefillingList extends InstanceList {
	public void add(String[] parameter) {
		list.add(new Refilling(parameter, this));
	}

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		String print = "";
		if (list.isEmpty()) {
			print = "no entries";
		}
		else {
			for (Instance instance : list) {
				Refilling refilling = (Refilling) instance;
				print = print + refilling.output() + "\r\n";
			}
		}
		return print;
	}

	public String initialOutput() {
		String output = "";
		if (!list.isEmpty()) {
			output = "[" + list.size() + "] " + "distance: " + "[" + getDistanceTotal() + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal() + " l" + "] " + "averageConsumption: " + "["
					+ getAverageConsumptionTotal() + " l/km" + "]";
		}
		return output;
	}

	private double getDistanceTotal() {
		double distanceTotal = 0;
		for (Instance instance : list) {
			Refilling refilling = (Refilling) instance;
			distanceTotal = distanceTotal + refilling.distance;
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal() {
		double refuelAmountTotal = 0;
		for (Instance instance : list) {
			Refilling refilling = (Refilling) instance;
			refuelAmountTotal = refuelAmountTotal + refilling.refuelAmount;
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}

	private double getAverageConsumptionTotal() {
		return Math.round((getRefuelAmountTotal() / getDistanceTotal()) * 1000.0) / 10.0;
	}

	protected double getHighestDistance() {
		double highestDistance = 0;
		for (Instance instance : list) {
			Refilling refilling = (Refilling) instance;
			if (refilling.distance > highestDistance) {
				highestDistance = refilling.distance;
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount() {
		double highestRefuelAmount = 0;
		for (Instance instance : list) {
			Refilling refilling = (Refilling) instance;
			if (refilling.refuelAmount > highestRefuelAmount) {
				highestRefuelAmount = refilling.refuelAmount;
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	protected double getHighestAverageConsumption() {
		double highestConsumption = 0;
		double currentConsumption;
		for (Instance instance : list) {
			Refilling refilling = (Refilling) instance;
			currentConsumption = Math.round((refilling.refuelAmount / refilling.distance) * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}
}