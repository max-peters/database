package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import database.plugin.Instance;

public class Subject extends Instance {
	public Subject(Map<String, String> parameter) {
		super(parameter);
		parameter.putIfAbsent("score", "0");
		parameter.putIfAbsent("maxPoints", "0");
		parameter.putIfAbsent("counter", "0");
	}

	private Integer getCounter() {
		return Integer.valueOf(getParameter("counter"));
	}

	private int getCounterStringLength() {
		return String.valueOf(getCounter()).length();
	}

	private Double getMaxPoints() {
		return Double.valueOf(getParameter("maxPoints"));
	}

	private String getName() {
		return getParameter("name");
	}

	private Double getScore() {
		return Double.valueOf(getParameter("score"));
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

	protected double calcPercent() {
		return getScore() / getMaxPoints() * 100;
	}

	protected String getTag() {
		return getParameter("tag");
	}

	protected String output(ArrayList<Instance> instances) {
		DecimalFormat oneDecimalPlace = new DecimalFormat("#0.0");
		DecimalFormat twoDecimalPlace = new DecimalFormat("#0.00");
		String toPrint;
		int allGradesLength = 0;
		int allGradesTotalLength = 0;
		int percentLength = 0;
		int counterLength = 0;
		int nameLength = 0;
		for (Instance instance : instances) {
			Subject current = (Subject) instance;
			allGradesLength = oneDecimalPlace.format(current.getScore()).length() > allGradesLength ? oneDecimalPlace.format(current.getScore()).length() : allGradesLength;
			allGradesTotalLength = oneDecimalPlace.format(current.getMaxPoints()).length() > allGradesTotalLength	? oneDecimalPlace.format(current.getMaxPoints()).length()
																													: allGradesTotalLength;
			percentLength = twoDecimalPlace.format(current.calcPercent()).length() > percentLength ? twoDecimalPlace.format(current.calcPercent()).length() : percentLength;
			counterLength = current.getCounterStringLength() > counterLength ? current.getCounterStringLength() : counterLength;
			nameLength = current.getName().length() > nameLength ? current.getName().length() : nameLength;
		}
		toPrint = getName();
		for (toPrint.length(); toPrint.length() < nameLength + 4;) {
			toPrint = toPrint + " ";
		}
		if (getCounter() != 0) {
			toPrint = toPrint+ "[" + String.format("%" + allGradesLength + "s", oneDecimalPlace.format(getScore())) + "/"
						+ String.format("%" + allGradesTotalLength + "s", oneDecimalPlace.format(getMaxPoints())) + " - "
						+ String.format("%" + percentLength + "s", twoDecimalPlace.format(calcPercent())) + "%" + "]" + " in ["
						+ String.format("%" + counterLength + "s", getCounter()).replace(' ', '0') + "] ";
			if (getCounter() == 1) {
				toPrint += "Blatt";
			}
			else {
				toPrint += "Bl\u00e4tter";
			}
		}
		else {
			toPrint += "keine Bl\u00e4tter";
		}
		return toPrint;
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		setCounter(getCounter() + 1);
		setMaxPoints(getMaxPoints() + newMaxPoint);
		setScore(getScore() + newScore);
	}
}