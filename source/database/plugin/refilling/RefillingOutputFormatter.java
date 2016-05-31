package database.plugin.refilling;

import java.util.Iterator;
import database.plugin.Command;
import database.plugin.OutputFormatter;

public class RefillingOutputFormatter extends OutputFormatter<Refilling> {
	@Command(tag = "all") public String outputAll(Iterable<Refilling> iterable) {
		StringBuilder builder = new StringBuilder();
		if (!iterable.iterator().hasNext()) {
			builder.append("no entries");
		}
		else {
			for (Refilling refilling : iterable) {
				builder.append(" \u25AA distance: ["+ String.format("%" + String.valueOf(getHighestDistance(iterable)).length() + "s", refilling.distance)
								+ " km] refuelAmount: [");
				builder.append(String.format("%"+ String.valueOf(String.format("%.1f", getHighestRefuelAmount(iterable))).length() + "s",
												String.format("%.1f", refilling.refuelAmount))
										.replace(",", "."));
				builder.append(" l] averageConsumption: ["
								+ String.format("%" + String.valueOf(getHighestAverageConsumption(iterable)).length() + "s", refilling.calcAverageConsumption()) + " l/km]"
								+ System.getProperty("line.separator"));
			}
		}
		return builder.toString();
	}

	protected double getHighestAverageConsumption(Iterable<Refilling> iterable) {
		double highestConsumption = 0;
		double currentConsumption;
		for (Refilling refilling : iterable) {
			currentConsumption = Math.round(refilling.refuelAmount / refilling.distance * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}

	protected double getHighestDistance(Iterable<Refilling> iterable) {
		double highestDistance = 0;
		for (Refilling refilling : iterable) {
			if (refilling.distance > highestDistance) {
				highestDistance = refilling.distance;
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount(Iterable<Refilling> iterable) {
		double highestRefuelAmount = 0;
		for (Refilling refilling : iterable) {
			if (refilling.refuelAmount > highestRefuelAmount) {
				highestRefuelAmount = refilling.refuelAmount;
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	@Override protected String getInitialOutput(Iterable<Refilling> iterable) {
		String output = "";
		if (iterable.iterator().hasNext()) {
			int sum = 0;
			Iterator<Refilling> it = iterable.iterator();
			while (it.hasNext()) {
				sum++;
				it.next();
			}
			output = "["+ sum + "] " + "distance: " + "[" + getDistanceTotal(iterable) + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal(iterable) + " l" + "] "
						+ "averageConsumption: " + "[" + getAverageConsumptionTotal(iterable) + " l/km" + "]";
		}
		return output;
	}

	private double getAverageConsumptionTotal(Iterable<Refilling> iterable) {
		return Math.round(getRefuelAmountTotal(iterable) / getDistanceTotal(iterable) * 1000.0) / 10.0;
	}

	private double getDistanceTotal(Iterable<Refilling> iterable) {
		double distanceTotal = 0;
		for (Refilling refilling : iterable) {
			distanceTotal = distanceTotal + refilling.distance;
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal(Iterable<Refilling> iterable) {
		double refuelAmountTotal = 0;
		for (Refilling refilling : iterable) {
			refuelAmountTotal = refuelAmountTotal + refilling.refuelAmount;
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}
}