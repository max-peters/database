package database.plugin.event.birthday;

import java.util.ArrayList;

import database.main.Instance;
import database.plugin.event.Event;
import database.plugin.event.EventList;

public class BirthdayList extends EventList {
	public ArrayList<Birthday> getList() {
		ArrayList<Birthday> birthdays = new ArrayList<Birthday>();
		for (Instance instance : list) {
			birthdays.add((Birthday) instance);
		}
		return birthdays;
	}

	public void add(String[] parameter) {
		list.add(new Birthday(parameter, this));
	}

	public void change(String[] parameter) {
	}

	public String output(String[] tags) {
		return null;
	}

	public String initialOutput() {
		String output = "";
		for (Event event : getNearEvents()) {
			Birthday birthday = (Birthday) event;
			output = output + birthday.output() + "\r\n";
		}
		if (!output.isEmpty()) {
			output = "birthdays:" + "\r\n" + output;
		}
		return output;
	}
}