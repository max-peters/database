package database.plugin.subject;

import java.util.ArrayList;

import database.main.Instance;
import database.main.InstanceList;

public class SubjectList extends InstanceList {
	public ArrayList<Subject> getList() {
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		for (Instance instance : list) {
			subjects.add((Subject) instance);
		}
		return subjects;
	}

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
			ArrayList<Subject> list = new ArrayList<Subject>();
			list.add(subject);
			return subject.output(list);
		}
	}

	public String initialOutput() {
		String output = "";
		for (Subject subject : getList()) {
			if (getList().indexOf(subject) != 0) {
				output = output + "\r\n";
			}
			output = output + subject.output(getList());
		}
		if (!output.isEmpty()) {
			output = "subjects:" + "\r\n" + output;
		}
		return output;
	}

	private String getAverage() {
		double sumPercentages = 0;
		double averagePercentage;
		int currentCounter = 0;
		for (Subject subject : getList()) {
			sumPercentages += subject.calcPercent();
			currentCounter++;
		}
		averagePercentage = sumPercentages / currentCounter;
		averagePercentage = Math.round(100.0 * averagePercentage) / 100.0;
		return "averagePercentage:" + averagePercentage + "%";
	}

	protected Subject getSubject(String tag) {
		Subject wanted = null;
		for (Subject subject : getList()) {
			if (subject.tag.equals(tag)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	protected String getTagsAsRegex() {
		String regex = "(";
		for (Subject subject : getList()) {
			regex = regex + subject.tag;
			if (!(getList().indexOf(subject) == getList().size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}
}