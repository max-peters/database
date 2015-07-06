package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import database.plugin.Instance;

public class Subject extends Instance {
	private double	allGrades;
	private double	allGradesTotal;
	private int		counter;
	public String	tag;
	public String	name;

	public Subject(String[] parameter, SubjectList list) {
		super(parameter[0], list);
		this.name = parameter[0];
		this.tag = parameter[1];
		if (parameter.length == 5) {
			this.allGrades = Double.parseDouble(parameter[2]);
			this.allGradesTotal = Double.parseDouble(parameter[3]);
			this.counter = Integer.parseInt(parameter[4]);
		}
		else {
			this.allGrades = 0;
			this.allGradesTotal = 0;
			this.counter = 0;
		}
	}

	public String toString() {
		return "subject" + " : " + name + " / " + tag + " / " + allGrades + " / " + allGradesTotal + " / " + counter;
	}

	protected double calcPercent() {
		return (allGrades / allGradesTotal) * 100;
	}

	protected void setGrade(double newGrade, double newGradeTotal) {
		counter++;
		allGradesTotal = newGradeTotal + allGradesTotal;
		allGrades = newGrade + allGrades;
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
		int allGradesLength = oneDecimalPlace.format(subjects.get(0).allGrades).length();
		int allGradesTotalLength = oneDecimalPlace.format(subjects.get(0).allGradesTotal).length();
		int percentLength = twoDecimalPlace.format(subjects.get(0).calcPercent()).length();
		int counterLength = String.valueOf(subjects.get(0).counter).length();
		int nameLength = subjects.get(0).name.length();
		for (Subject current : subjects) {
			currentLength = oneDecimalPlace.format(current.allGrades).length();
			if (allGradesLength < currentLength)
				allGradesLength = currentLength;
		}
		for (Subject current : subjects) {
			currentLength = oneDecimalPlace.format(current.allGradesTotal).length();
			if (allGradesTotalLength < currentLength)
				allGradesTotalLength = currentLength;
		}
		for (Subject current : subjects) {
			currentLength = twoDecimalPlace.format(current.calcPercent()).length();
			if (percentLength < currentLength)
				percentLength = currentLength;
		}
		for (Subject current : subjects) {
			if (counterLength < String.valueOf(current.counter).length())
				counterLength = String.valueOf(current.counter).length();
		}
		for (Subject current : subjects) {
			if (nameLength < current.name.length())
				nameLength = current.name.length();
		}
		nameLength = nameLength + 4;
		newName = name;
		for (newName.length(); newName.length() < nameLength;) {
			newName = newName + " ";
		}
		toPrint = newName;
		if (counter > 0) {
			toPrint = toPrint + "[" + String.format("%" + allGradesLength + "s", oneDecimalPlace.format(allGrades)) + "/"
					+ String.format("%" + allGradesTotalLength + "s", oneDecimalPlace.format(allGradesTotal)) + " - " + String.format("%" + percentLength + "s", twoDecimalPlace.format(calcPercent()))
					+ "%" + "]" + " in [" + String.format("%" + counterLength + "s", counter).replace(' ', '0');
			if (counter == 1) {
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