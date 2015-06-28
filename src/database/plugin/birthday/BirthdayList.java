package database.plugin.birthday;

import java.util.ArrayList;

import database.main.Instance;
import database.main.InstanceList;

public class BirthdayList extends InstanceList {
	public BirthdayList() {
	}

	public ArrayList<Birthday> getList() {
		ArrayList<Birthday> birthdays = new ArrayList<Birthday>();
		for (Instance instance : list) {
			birthdays.add((Birthday) instance);
		}
		return birthdays;
	}

	public String initialOutput() {
		String output = "";
		for (Birthday birthday : getList()) {
			if (!output.isEmpty()) {
				output = output + "\r\n";
			}
			output = output + birthday.singleOutput();
		}
		if (!output.isEmpty()) {
			output = "birthdays:" + "\r\n" + output;
		}
		return output;
	}

	public String output(String[] tags) {
		return null;
	}

	public void add(String[] parameter) {
		list.add(new Birthday(parameter, this));
	}
}