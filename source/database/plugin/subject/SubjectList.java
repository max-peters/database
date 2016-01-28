package database.plugin.subject;

import java.text.DecimalFormat;
import java.util.Map;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstanceList;

public class SubjectList extends InstanceList {
	@Override public void add(Map<String, String> map) {
		list.add(new Subject(map));
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
				int currentWorksheetCounterStringLength = String.valueOf(current.worksheetCounter).length();
				allGradesLength = oneDecimalPlace.format(current.score).length() > allGradesLength ? oneDecimalPlace.format(current.score).length() : allGradesLength;
				allGradesTotalLength = oneDecimalPlace.format(current.maxPoints).length() > allGradesTotalLength	? oneDecimalPlace.format(current.maxPoints).length()
																													: allGradesTotalLength;
				percentLength = twoDecimalPlace.format(current.calcPercent()).length() > percentLength ? twoDecimalPlace.format(current.calcPercent()).length() : percentLength;
				counterLength = currentWorksheetCounterStringLength > counterLength ? currentWorksheetCounterStringLength : counterLength;
				nameLength = current.name.length() > nameLength ? current.name.length() : nameLength;
			}
			for (Instance currentInstance : getIterable()) {
				Subject subject = (Subject) currentInstance;
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

	protected Subject getSubject(String tag) {
		Subject wanted = null;
		for (Instance instance : getIterable()) {
			Subject subject = (Subject) instance;
			if (subject.tag.equals(tag)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	protected String getTagsAsRegex() {
		String regex = "(";
		for (Instance instance : getIterable()) {
			Subject subject = (Subject) instance;
			regex += subject.tag + "|";
		}
		return regex.substring(0, regex.lastIndexOf("|")) + ")";
	}
}