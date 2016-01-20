package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.Map;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class SubjectList extends InstanceList {
	@Override public void add(Map<String, String> parameter) {
		list.add(new Subject(parameter));
	}

	@Command(tag = "average") public String getAverage() {
		double sumPercentages = 0;
		double averagePercentage;
		int currentCounter = 0;
		for (Instance instance : getIterable()) {
			Subject subject = (Subject) instance;
			sumPercentages += subject.calcPercent();
			currentCounter++;
		}
		averagePercentage = sumPercentages / currentCounter;
		averagePercentage = Math.round(100.0 * averagePercentage) / 100.0;
		return "average percentage : " + averagePercentage + "%";
	}

	@Override public String initialOutput() {
		return outputAll();
	}

	@Command(tag = "all") public String outputAll() {
		DecimalFormat oneDecimalPlace = new DecimalFormat("#0.0");
		DecimalFormat twoDecimalPlace = new DecimalFormat("#0.00");
		StringBuilder builder = new StringBuilder();
		int allGradesLength = 0;
		int allGradesTotalLength = 0;
		int percentLength = 0;
		int counterLength = 0;
		int nameLength = 0;
		if (isEmpty()) {
			builder.append("keine Blätter");
		}
		else {
			for (Instance instance : getIterable()) {
				Subject current = (Subject) instance;
				allGradesLength = oneDecimalPlace.format(current.getScore()).length() > allGradesLength ? oneDecimalPlace.format(current.getScore()).length() : allGradesLength;
				allGradesTotalLength = oneDecimalPlace.format(current.getMaxPoints()).length() > allGradesTotalLength	? oneDecimalPlace.format(current.getMaxPoints()).length()
																														: allGradesTotalLength;
				percentLength = twoDecimalPlace.format(current.calcPercent()).length() > percentLength ? twoDecimalPlace.format(current.calcPercent()).length() : percentLength;
				counterLength = current.getCounterStringLength() > counterLength ? current.getCounterStringLength() : counterLength;
				nameLength = current.getName().length() > nameLength ? current.getName().length() : nameLength;
			}
			for (Instance currentInstance : getIterable()) {
				Subject subject = (Subject) currentInstance;
				builder.append(subject.getName());
				for (int i = subject.getName().length(); i < nameLength + 4; i++) {
					builder.append(" ");
				}
				builder.append("["+ String.format("%" + allGradesLength + "s", oneDecimalPlace.format(subject.getScore())) + "/"
								+ String.format("%" + allGradesTotalLength + "s", oneDecimalPlace.format(subject.getMaxPoints())) + " - "
								+ String.format("%" + percentLength + "s", twoDecimalPlace.format(subject.calcPercent())) + "%" + "]" + " in ["
								+ String.format("%" + counterLength + "s", subject.getCounter()).replace(' ', '0') + "] ");
				if (subject.getCounter() == 1) {
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

	protected Subject getSubject(String tag) {
		Subject wanted = null;
		for (Instance instance : getIterable()) {
			Subject subject = (Subject) instance;
			if (subject.getTag().equals(tag)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	protected String getTagsAsRegex() {
		String regex = "(";
		for (Instance instance : getIterable()) {
			Subject subject = (Subject) instance;
			regex += subject.getTag() + "|";
		}
		return regex.substring(0, regex.lastIndexOf("|")) + ")";
	}
}