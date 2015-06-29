package database.plugin.refilling;

import java.util.ArrayList;

import database.main.Instance;
import database.main.InstanceList;

public class RefillingList extends InstanceList {
	public ArrayList<Refilling> getList() {
		ArrayList<Refilling> refillings = new ArrayList<Refilling>();
		for (Instance instance : list) {
			refillings.add((Refilling) instance);
		}
		return refillings;
	}

	public void add(String[] parameter) {
		list.add(new Refilling(parameter, this));
	}

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		String print = "";
		if (getList().isEmpty()) {
			print = "no entries";
		}
		else {
			for (Refilling refilling : getList()) {
				print = print + refilling.output() + "\r\n";
			}
		}
		return print;
	}

	public String initialOutput() {
		String output = "";
		if (!getList().isEmpty()) {
			output = "[" + getList().size() + "] " + "distance: " + "[" + getDistanceTotal() + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal() + " l" + "] " + "averageConsumption: "
					+ "[" + getAverageConsumptionTotal() + " l/km" + "]";
		}
		if (!output.isEmpty()) {
			output = "refillings:" + "\r\n" + output;
		}
		return output;
	}

	private double getDistanceTotal() {
		double distanceTotal = 0;
		for (Refilling refilling : getList()) {
			distanceTotal = distanceTotal + refilling.distance;
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal() {
		double refuelAmountTotal = 0;
		for (Refilling refilling : getList()) {
			refuelAmountTotal = refuelAmountTotal + refilling.refuelAmount;
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}

	private double getAverageConsumptionTotal() {
		return Math.round((getRefuelAmountTotal() / getDistanceTotal()) * 1000.0) / 10.0;
	}

	protected double getHighestDistance() {
		double highestDistance = 0;
		for (Refilling refilling : getList()) {
			if (refilling.distance > highestDistance) {
				highestDistance = refilling.distance;
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount() {
		double highestRefuelAmount = 0;
		for (Refilling refilling : getList()) {
			if (refilling.refuelAmount > highestRefuelAmount) {
				highestRefuelAmount = refilling.refuelAmount;
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	protected double getHighestAverageConsumption() {
		double highestConsumption = 0;
		double currentConsumption;
		for (Refilling refilling : getList()) {
			currentConsumption = Math.round((refilling.refuelAmount / refilling.distance) * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}
}