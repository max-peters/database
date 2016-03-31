package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.LinkedList;
import database.plugin.Command;
import database.plugin.OutputFormatter;

public class SubjectOutputFormatter extends OutputFormatter<Subject> {
	@Command(tag = "average") public String getAverage(LinkedList<Subject> list) {
		double sumPercentages = 0;
		double averagePercentage;
		int currentCounter = 0;
		for (Subject subject : list) {
			sumPercentages += subject.calcPercent();
			currentCounter++;
		}
		averagePercentage = sumPercentages / currentCounter;
		averagePercentage = Math.round(100.0 * averagePercentage) / 100.0;
		return "average percentage : " + averagePercentage + "%";
	}

	@Command(tag = "all") public String outputAll(LinkedList<Subject> list) {
		DecimalFormat oneDecimalPlace = new DecimalFormat("#0.0");
		DecimalFormat twoDecimalPlace = new DecimalFormat("#0.00");
		StringBuilder builder = new StringBuilder();
		int allGradesLength = 0;
		int allGradesTotalLength = 0;
		int percentLength = 0;
		int counterLength = 0;
		int nameLength = 0;
		if (list.isEmpty()) {
			builder.append("keine Blätter");
		}
		else {
			for (Subject subject : list) {
				int currentWorksheetCounterStringLength = String.valueOf(subject.worksheetCounter).length();
				allGradesLength = oneDecimalPlace.format(subject.score).length() > allGradesLength ? oneDecimalPlace.format(subject.score).length() : allGradesLength;
				allGradesTotalLength = oneDecimalPlace.format(subject.maxPoints).length() > allGradesTotalLength	? oneDecimalPlace.format(subject.maxPoints).length()
																													: allGradesTotalLength;
				percentLength = twoDecimalPlace.format(subject.calcPercent()).length() > percentLength ? twoDecimalPlace.format(subject.calcPercent()).length() : percentLength;
				counterLength = currentWorksheetCounterStringLength > counterLength ? currentWorksheetCounterStringLength : counterLength;
				nameLength = subject.name.length() > nameLength ? subject.name.length() : nameLength;
			}
			for (Subject subject : list) {
				builder.append(subject.name);
				for (int i = subject.name.length(); i < nameLength + 4; i++) {
					builder.append(" ");
				}
				builder.append("["+ String.format("%" + allGradesLength + "s", oneDecimalPlace.format(subject.score)) + "/"
								+ String.format("%" + allGradesTotalLength + "s", oneDecimalPlace.format(subject.maxPoints)) + " - "
								+ String.format("%" + percentLength + "s", twoDecimalPlace.format(subject.calcPercent())) + "%" + "]" + " in ["
								+ String.format("%" + counterLength + "s", subject.worksheetCounter).replace(' ', '0') + "] ");
				if (subject.worksheetCounter == 1) {
					builder.append("Blatt");
				}
				else {
					builder.append("Bl\u00e4tter");
				}
				builder.append(System.getProperty("line.separator"));
			}
		}
		return builder.toString();
	}

	@Override protected String getInitialOutput(LinkedList<Subject> list) {
		return outputAll(list);
	}
}