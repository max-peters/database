package database.plugin.refilling;

import java.time.LocalDate;

import database.plugin.Instance;

public class Refilling extends Instance {
	private Double cost;
	private LocalDate date;
	private Double distance;
	private Double refuelAmount;

	public Refilling(Double distance, Double refuelAmount, Double cost, LocalDate date) {
		this.distance = distance;
		this.refuelAmount = refuelAmount;
		this.cost = cost;
		this.date = date;
	}

	public double calcAverageConsumption() {
		return Math.round(refuelAmount / distance * 1000) / 10.0;
	}

	public Double getCost() {
		return cost;
	}

	public LocalDate getDate() {
		return date;
	}

	public Double getDistance() {
		return distance;
	}

	public Double getRefuelAmount() {
		return refuelAmount;
	}
}