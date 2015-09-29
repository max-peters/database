package database.plugin.subject;

import java.util.Map;

import database.plugin.Instance;
import database.plugin.InstanceList;

public class SubjectList extends InstanceList {
	@Override public void add(Map<String, String> parameter) {
		getList().add(new Subject(parameter, this));
	}

	@Override public String output(Map<String, String> map) {
		String returnValue = null;
		for (String string : map.values()) {
			if (string.equals("average")) {
				returnValue = getAverage();
			}
			else {
				Subject subject = getSubject(string);
				returnValue = subject.output(getList());
			}
		}
		return returnValue;
	}

	@Override public String initialOutput() {
		String output = "";
		for (Instance instance : getList()) {
			Subject subject = (Subject) instance;
			if (getList().indexOf(subject) != 0) {
				output = output + "\r\n";
			}
			output = output + subject.output(getList());
		}
		return output;
	}

	private String getAverage() {
		double sumPercentages = 0;
		double averagePercentage;
		int currentCounter = 0;
		for (Instance instance : getList()) {
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
		for (Instance instance : getList()) {
			Subject subject = (Subject) instance;
			if (subject.getTag().equals(tag)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	protected String getTagsAsRegex() {
		String regex = "(";
		for (Instance instance : getList()) {
			Subject subject = (Subject) instance;
			regex = regex + subject.getTag();
			if (!(getList().indexOf(subject) == getList().size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}
}