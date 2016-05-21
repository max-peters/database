package database.plugin.subject;

import database.plugin.Instance;

public class Subject extends Instance {
	public int		counter;
	public Double	maxPoints;
	public String	name;
	public Double	score;
	public String	tag;

	public Subject(String name, String tag, Double score, Double maxPoints, int counter) {
		this.name = name;
		this.tag = tag;
		this.score = score;
		this.maxPoints = maxPoints;
		this.counter = counter;
	}

	protected double calcPercent() {
		return score / maxPoints * 100;
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		counter++;
		maxPoints += newMaxPoint;
		score += newScore;
	}
}