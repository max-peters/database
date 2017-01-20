package database.plugin.element;

import database.plugin.Instance;

public class Subject extends Instance {
	public int		counter;
	public Double	maxPoints;
	public String	name;
	public Double	score;

	public Subject(String name, Double score, Double maxPoints, int counter) {
		this.name = name;
		this.score = score;
		this.maxPoints = maxPoints;
		this.counter = counter;
	}

	public Subject withNewGrade(double newScore, double newMaxPoint) {
		return new Subject(name, score + newScore, maxPoints + newMaxPoint, counter + 1);
	}
}