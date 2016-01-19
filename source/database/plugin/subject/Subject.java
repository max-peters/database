package database.plugin.subject;

import java.util.Map;
import database.plugin.Instance;

public class Subject extends Instance {
	public Subject(Map<String, String> parameter) {
		super(parameter);
		parameter.putIfAbsent("score", "0");
		parameter.putIfAbsent("maxPoints", "0");
		parameter.putIfAbsent("counter", "0");
	}

	public Integer getCounter() {
		return Integer.valueOf(getParameter("counter"));
	}

	public int getCounterStringLength() {
		return String.valueOf(getCounter()).length();
	}

	public Double getMaxPoints() {
		return Double.valueOf(getParameter("maxPoints"));
	}

	public String getName() {
		return getParameter("name");
	}

	public Double getScore() {
		return Double.valueOf(getParameter("score"));
	}

	protected double calcPercent() {
		return getScore() / getMaxPoints() * 100;
	}

	protected String getTag() {
		return getParameter("tag");
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		setCounter(getCounter() + 1);
		setMaxPoints(getMaxPoints() + newMaxPoint);
		setScore(getScore() + newScore);
	}

	private void setCounter(int counter) {
		setParameter("counter", String.valueOf(counter));
	}

	private void setMaxPoints(double maxPoints) {
		setParameter("maxPoints", String.valueOf(maxPoints));
	}

	private void setScore(double score) {
		setParameter("score", String.valueOf(score));
	}
}