package database.plugin.subject;

import database.plugin.Instance;
import database.plugin.InstanceList;

public class SubjectList extends InstanceList {
	public void add(String[] parameter) {
		list.add(new Subject(parameter, this));
	}

	public void change(String[] parameter) {
		Subject toChange = getSubject(parameter[0]);
		toChange.setGrade(Double.parseDouble(parameter[1]), Double.parseDouble(parameter[2]));
	}

	public String output(String[] tags) {
		if (tags[0].equals("average")) {
			return getAverage();
		}
		else {
			Subject subject = getSubject(tags[0]);
			return subject.output(list);
		}
	}

	public String initialOutput() {
		String output = "";
		for (Instance instance : list) {
			Subject subject = (Subject) instance;
			if (list.indexOf(subject) != 0) {
				output = output + "\r\n";
			}
			output = output + subject.output(list);
		}
		return output;
	}

	private String getAverage() {
		double sumPercentages = 0;
		double averagePercentage;
		int currentCounter = 0;
		for (Instance instance : list) {
			Subject subject = (Subject) instance;
			sumPercentages += subject.calcPercent();
			currentCounter++;
		}
		averagePercentage = sumPercentages / currentCounter;
		averagePercentage = Math.round(100.0 * averagePercentage) / 100.0;
		return "averagePercentage:" + averagePercentage + "%";
	}

	protected Subject getSubject(String tag) {
		Subject wanted = null;
		for (Instance instance : list) {
			Subject subject = (Subject) instance;
			if (subject.tag.equals(tag)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	protected String getTagsAsRegex() {
		String regex = "(";
		for (Instance instance : list) {
			Subject subject = (Subject) instance;
			regex = regex + subject.tag;
			if (!(list.indexOf(subject) == list.size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}
}