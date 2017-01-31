package database.plugin.subject;

import database.plugin.Instance;

public class Subject extends Instance {
	private int		counter;
	private Double	maxPoints;
	private String	name;
	private Double	score;

	public Subject(String name, Double score, Double maxPoints, int counter) {
		this.name = name;
		this.score = score;
		this.maxPoints = maxPoints;
		this.counter = counter;
	}

	public int getCounter() {
		return counter;
	}

	public Double getMaxPoints() {
		return maxPoints;
	}

	public String getName() {
		return name;
	}

	public Double getScore() {
		return score;
	}
}