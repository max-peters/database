package database.plugin.subject;

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
}