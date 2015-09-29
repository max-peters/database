package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import database.plugin.Instance;

public class Subject extends Instance {
	public Subject(Map<String, String> parameter, SubjectList list) {
		super(parameter, "subject", list);
		parameter.put("score", "0");
		parameter.put("maxPoints", "0");
		parameter.put("counter", "0");
	}

	@Getter Integer getCounter() {
		return Integer.valueOf(getParameter("counter"));
	}

	@Getter Double getMaxPoints() {
		return Double.valueOf(getParameter("maxPoints"));
	}

	@Getter Double getScore() {
		return Double.valueOf(getParameter("score"));
	}

	@Getter String getName() {
		return getParameter("name");
	}

	@Getter String getTag() {
		return getParameter("tag");
	}

	@Setter void setCounter(int counter) {
		setParameter("counter", String.valueOf(counter));
	}

	@Setter void setMaxPoints(double maxPoints) {
		setParameter("maxPoints", String.valueOf(maxPoints));
	}

	@Setter void setScore(double score) {
		setParameter("score", String.valueOf(score));
	}

	protected double calcPercent() {
		return (getScore() / getMaxPoints()) * 100;
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		setCounter(getCounter() + 1);
		setMaxPoints(getMaxPoints() + newMaxPoint);
		setScore(getScore() + newScore);
	}

	protected String output(ArrayList<Instance> instances) {
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		for (Instance instance : instances) {
			subjects.add((Subject) instance);
		}
		DecimalFormat oneDecimalPlace = new DecimalFormat("#0.0");
		DecimalFormat twoDecimalPlace = new DecimalFormat("#0.00");
		String toPrint;
		String newName;
		int currentLength;
		int allGradesLength = oneDecimalPlace.format(subjects.get(0).getScore()).length();
		int allGradesTotalLength = oneDecimalPlace.format(subjects.get(0).getMaxPoints()).length();
		int percentLength = twoDecimalPlace.format(subjects.get(0).calcPercent()).length();
		int counterLength = subjects.get(0).getParameter("counter").length();
		int nameLength = subjects.get(0).getName().length();
		for (Subject current : subjects) {
			currentLength = oneDecimalPlace.format(current.getScore()).length();
			if (allGradesLength < currentLength)
				allGradesLength = currentLength;
		}
		for (Subject current : subjects) {
			currentLength = oneDecimalPlace.format(current.getMaxPoints()).length();
			if (allGradesTotalLength < currentLength)
				allGradesTotalLength = currentLength;
		}
		for (Subject current : subjects) {
			currentLength = twoDecimalPlace.format(current.calcPercent()).length();
			if (percentLength < currentLength)
				percentLength = currentLength;
		}
		for (Subject current : subjects) {
			if (counterLength < current.getParameter("counter").length())
				counterLength = current.getParameter("counter").length();
		}
		for (Subject current : subjects) {
			if (nameLength < current.getName().length())
				nameLength = current.getName().length();
		}
		nameLength = nameLength + 4;
		newName = getName();
		for (newName.length(); newName.length() < nameLength;) {
			newName = newName + " ";
		}
		toPrint = newName;
		if (getCounter() > 0) {
			toPrint = toPrint + "[" + String.format("%" + allGradesLength + "s", oneDecimalPlace.format(getScore())) + "/"
					+ String.format("%" + allGradesTotalLength + "s", oneDecimalPlace.format(getMaxPoints())) + " - " + String.format("%" + percentLength + "s", twoDecimalPlace.format(calcPercent()))
					+ "%" + "]" + " in [" + String.format("%" + counterLength + "s", getCounter()).replace(' ', '0');
			if (getCounter() == 1) {
				toPrint = toPrint + "] Blatt";
			}
			else {
				toPrint = toPrint + "] Bl\u00e4tter";
			}
		}
		else {
			toPrint = toPrint + "keine Bl\u00e4tter";
		}
		return toPrint;
	}
}