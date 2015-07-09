package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import database.plugin.Instance;

public class Subject extends Instance {
	public Subject(String[][] parameter, SubjectList list) {
		super(parameter, parameter[2][1], list);
	}

	@Override public String[][] getParameter() {
		return new String[][] { { "name", getParameter("name") }, { "tag", getParameter("tag") }, { "score", getParameter("score") }, { "maxPoints", getParameter("maxPoints") },
				{ "counter", getParameter("counter") } };
	}

	protected double calcPercent() {
		return (Double.valueOf(getParameter("score")) / Double.valueOf(getParameter("maxPoints"))) * 100;
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		setParameter("counter", String.valueOf(Integer.valueOf(getParameter("counter")) + 1));
		setParameter("maxPoints", String.valueOf(Double.valueOf(getParameter("maxPoints")) + newMaxPoint));
		setParameter("score", String.valueOf(Double.valueOf(getParameter("score")) + newScore));
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
		int allGradesLength = oneDecimalPlace.format(Double.valueOf(subjects.get(0).getParameter("score"))).length();
		int allGradesTotalLength = oneDecimalPlace.format(Double.valueOf(subjects.get(0).getParameter("maxPoints"))).length();
		int percentLength = twoDecimalPlace.format(subjects.get(0).calcPercent()).length();
		int counterLength = String.valueOf(subjects.get(0).getParameter("counter")).length();
		int nameLength = subjects.get(0).getParameter("name").length();
		for (Subject current : subjects) {
			currentLength = oneDecimalPlace.format(Double.valueOf(current.getParameter("score"))).length();
			if (allGradesLength < currentLength)
				allGradesLength = currentLength;
		}
		for (Subject current : subjects) {
			currentLength = oneDecimalPlace.format(Double.valueOf(current.getParameter("maxPoints"))).length();
			if (allGradesTotalLength < currentLength)
				allGradesTotalLength = currentLength;
		}
		for (Subject current : subjects) {
			currentLength = twoDecimalPlace.format(current.calcPercent()).length();
			if (percentLength < currentLength)
				percentLength = currentLength;
		}
		for (Subject current : subjects) {
			if (counterLength < String.valueOf(current.getParameter("counter")).length())
				counterLength = String.valueOf(current.getParameter("counter")).length();
		}
		for (Subject current : subjects) {
			if (nameLength < current.getParameter("name").length())
				nameLength = current.getParameter("name").length();
		}
		nameLength = nameLength + 4;
		newName = getParameter("name");
		for (newName.length(); newName.length() < nameLength;) {
			newName = newName + " ";
		}
		toPrint = newName;
		if (Integer.valueOf(getParameter("counter")) > 0) {
			toPrint = toPrint + "[" + String.format("%" + allGradesLength + "s", oneDecimalPlace.format(Double.valueOf(getParameter("score")))) + "/"
					+ String.format("%" + allGradesTotalLength + "s", oneDecimalPlace.format(Double.valueOf(getParameter("maxPoints")))) + " - "
					+ String.format("%" + percentLength + "s", twoDecimalPlace.format(calcPercent())) + "%" + "]" + " in ["
					+ String.format("%" + counterLength + "s", getParameter("counter")).replace(' ', '0');
			if (Integer.valueOf(getParameter("counter")) == 1) {
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