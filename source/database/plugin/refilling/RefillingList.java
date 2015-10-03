package database.plugin.refilling;

import java.util.Map;

import database.plugin.Instance;
import database.plugin.InstanceList;
import database.plugin.expense.ExpenseList;

public class RefillingList extends InstanceList {
	private ExpenseList	expenseList;

	public RefillingList(ExpenseList expenseList) {
		this.expenseList = expenseList;
	}

	@Override public void add(Map<String, String> parameter) {
		getList().add(new Refilling(parameter, this, expenseList));
	}

	@Override public String output(Map<String, String> map) {
		String print = "";
		if (getList().isEmpty()) {
			print = "no entries";
		}
		else {
			for (Instance instance : getList()) {
				Refilling refilling = (Refilling) instance;
				print = print + refilling.output() + "\r\n";
			}
		}
		return print;
	}

	@Override public String initialOutput() {
		String output = "";
		if (!getList().isEmpty()) {
			output = "[" + getList().size() + "] " + "distance: " + "[" + getDistanceTotal() + " km" + "] " + "refuelAmount: " + "[" + getRefuelAmountTotal() + " l" + "] " + "averageConsumption: "
					+ "[" + getAverageConsumptionTotal() + " l/km" + "]";
		}
		return output;
	}

	private double getDistanceTotal() {
		double distanceTotal = 0;
		for (Instance instance : getList()) {
			Refilling refilling = (Refilling) instance;
			distanceTotal = distanceTotal + refilling.getDistance();
		}
		return Math.round(10.0 * distanceTotal) / 10.0;
	}

	private double getRefuelAmountTotal() {
		double refuelAmountTotal = 0;
		for (Instance instance : getList()) {
			Refilling refilling = (Refilling) instance;
			refuelAmountTotal = refuelAmountTotal + refilling.getRefuelAmount();
		}
		return Math.round(10.0 * refuelAmountTotal) / 10.0;
	}

	private double getAverageConsumptionTotal() {
		return Math.round((getRefuelAmountTotal() / getDistanceTotal()) * 1000.0) / 10.0;
	}

	protected double getHighestDistance() {
		double highestDistance = 0;
		for (Instance instance : getList()) {
			Refilling refilling = (Refilling) instance;
			if (refilling.getDistance() > highestDistance) {
				highestDistance = refilling.getDistance();
			}
		}
		return Math.round(10.0 * highestDistance) / 10.0;
	}

	protected double getHighestRefuelAmount() {
		double highestRefuelAmount = 0;
		for (Instance instance : getList()) {
			Refilling refilling = (Refilling) instance;
			if (refilling.getRefuelAmount() > highestRefuelAmount) {
				highestRefuelAmount = refilling.getRefuelAmount();
			}
		}
		return Math.round(10.0 * highestRefuelAmount) / 10.0;
	}

	protected double getHighestAverageConsumption() {
		double highestConsumption = 0;
		double currentConsumption;
		for (Instance instance : getList()) {
			Refilling refilling = (Refilling) instance;
			currentConsumption = Math.round((refilling.getRefuelAmount() / refilling.getDistance()) * 1000.0) / 10.0;
			if (currentConsumption > highestConsumption) {
				highestConsumption = currentConsumption;
			}
		}
		return highestConsumption;
	}
}