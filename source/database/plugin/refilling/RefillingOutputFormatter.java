package database.plugin.refilling;

import java.util.LinkedList;
import database.plugin.Command;
import database.plugin.OutputFormatter;

public class RefillingOutputFormatter extends OutputFormatter<Refilling> {
	@Command(tag = "all") public String outputAll(LinkedList<Refilling> list) {
		StringBuilder builder = new StringBuilder();
		if (list.isEmpty()) {
			builder.append("no entries");
		}
		else {
			for (Refilling refilling : list) {
				builder.append("[" + String.format("%" + String.valueOf(list.size()).length() + "s", list.indexOf(refilling)).replace(' ', '0'));
				builder.append("] distance: [" + String.format("%" + String.valueOf(getHighestDistance(list)).length() + "s", refilling.distance) + " km] refuelAmount: [");
				builder.append(String.format("%"+ String.valueOf(String.format("%.1f", getHighestRefuelAmount(list))).length() + "s",
												String.format("%.1f", refilling.refuelAmount))
										.replace(",", "."));
				builder.append(" l] averageConsumption: ["
								+ String.format("%" + String.valueOf(getHighestAverageConsumption(list)).length() + "s", refilling.calcAverageConsumption()) + " l/km]"
								+ System.getProperty("line.separator"));
			}
		}
		return builder.toString();
	}

	protected double getHighestAverageConsumption(LinkedList<Refilling> list) {
		double highestConsumption = 0;
		double currentConsumption;
		for (Refilling refilling : list) {
			currentConsumption = Math.round(refilling.refuelAmount / refilling.distance * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}

	protected double getHighestDistance(LinkedList<Refilling> list) {
		double highestDistance = 0;
		for (Refilling refilling : list) {
			if (refilling.distance > highestDistance) {
				highestDistance = refilling.distance;
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount(LinkedList<Refilling> list) {
		double highestRefuelAmount = 0;
		for (Refilling refilling : list) {
			if (refilling.refuelAmount > highestRefuelAmount) {
				highestRefuelAmount = refilling.refuelAmount;
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	@Override protected String getInitialOutput(LinkedList<Refilling> list) {
		String output = "";
		if (list.isEmpty()) {
			output = "no entries";
		}
		else {
			output = "["+ list.size() + "] " + "distance: " + "[" + getDistanceTotal(list) + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal(list) + " l" + "] "
						+ "averageConsumption: " + "[" + getAverageConsumptionTotal(list) + " l/km" + "]";
		}
		return output;
	}

	private double getAverageConsumptionTotal(LinkedList<Refilling> list) {
		return Math.round(getRefuelAmountTotal(list) / getDistanceTotal(list) * 1000.0) / 10.0;
	}

	private double getDistanceTotal(LinkedList<Refilling> list) {
		double distanceTotal = 0;
		for (Refilling refilling : list) {
			distanceTotal = distanceTotal + refilling.distance;
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal(LinkedList<Refilling> list) {
		double refuelAmountTotal = 0;
		for (Refilling refilling : list) {
			refuelAmountTotal = refuelAmountTotal + refilling.refuelAmount;
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}
}