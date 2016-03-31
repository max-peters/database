package database.plugin.refilling;

import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class RefillingList extends InstanceList<Refilling> {
	@Override public String initialOutput() {
		String output = "";
		if (isEmpty()) {
			output = "no entries";
		}
		else {
			output = "["+ size() + "] " + "distance: " + "[" + getDistanceTotal() + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal() + " l" + "] "
						+ "averageConsumption: " + "[" + getAverageConsumptionTotal() + " l/km" + "]";
		}
		return output;
	}

	@Command(tag = "all") public String outputAll() {
		StringBuilder builder = new StringBuilder();
		if (isEmpty()) {
			builder.append("no entries");
		}
		else {
			for (Instance instance : this) {
				Refilling refilling = (Refilling) instance;
				builder.append("[" + String.format("%" + String.valueOf(size()).length() + "s", indexOf(instance)).replace(' ', '0'));
				builder.append("] distance: [" + String.format("%" + String.valueOf(getHighestDistance()).length() + "s", refilling.distance) + " km] refuelAmount: [");
				builder.append(String	.format("%" + String.valueOf(String.format("%.1f", getHighestRefuelAmount())).length() + "s", String.format("%.1f", refilling.refuelAmount))
										.replace(",", "."));
				builder.append(" l] averageConsumption: ["+ String.format("%" + String.valueOf(getHighestAverageConsumption()).length() + "s", refilling.calcAverageConsumption())
								+ " l/km]" + System.getProperty("line.separator"));
			}
		}
		return builder.toString();
	}

	protected double getHighestAverageConsumption() {
		double highestConsumption = 0;
		double currentConsumption;
		for (Instance instance : this) {
			Refilling refilling = (Refilling) instance;
			currentConsumption = Math.round(refilling.refuelAmount / refilling.distance * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}

	protected double getHighestDistance() {
		double highestDistance = 0;
		for (Instance instance : this) {
			Refilling refilling = (Refilling) instance;
			if (refilling.distance > highestDistance) {
				highestDistance = refilling.distance;
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount() {
		double highestRefuelAmount = 0;
		for (Instance instance : this) {
			Refilling refilling = (Refilling) instance;
			if (refilling.refuelAmount > highestRefuelAmount) {
				highestRefuelAmount = refilling.refuelAmount;
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	private double getAverageConsumptionTotal() {
		return Math.round(getRefuelAmountTotal() / getDistanceTotal() * 1000.0) / 10.0;
	}

	private double getDistanceTotal() {
		double distanceTotal = 0;
		for (Instance instance : this) {
			Refilling refilling = (Refilling) instance;
			distanceTotal = distanceTotal + refilling.distance;
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal() {
		double refuelAmountTotal = 0;
		for (Instance instance : this) {
			Refilling refilling = (Refilling) instance;
			refuelAmountTotal = refuelAmountTotal + refilling.refuelAmount;
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}
}