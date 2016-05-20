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

	@Override public boolean equals(Object object) {
		Subject subject;
		if (object != null && object.getClass().equals(this.getClass())) {
			subject = (Subject) object;
			if (counter == subject.counter && name.equals(subject.name) && tag.equals(subject.tag) && score.equals(subject.score) && maxPoints.equals(subject.maxPoints)) {
				return true;
			}
		}
		return false;
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