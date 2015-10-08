package database.plugin.event.holiday;

import java.util.Map;

import database.main.date.Date;
import database.plugin.Instance;
import database.plugin.event.EventList;

public class HolidayList extends EventList {
	@Override public void add(Map<String, String> parameter) {
		if (getList().isEmpty() && new Date(parameter.get("date")).compareTo(Date.getCurrentDate()) >= 0) {
			sortedAdd(new Holiday(parameter, this));
		}
		else {
			boolean contains = false;
			for (Instance instance : getList()) {
				Holiday holiday = (Holiday) instance;
				if (holiday.getName().equals(parameter.get("name"))) {
					contains = true;
					if (holiday.getDate().compareTo(new Date(parameter.get("date"))) < 0 && holiday.getDate().compareTo(Date.getCurrentDate()) < 0) {
						holiday.getParameter().replace("date", parameter.get("date"));
						return;
					}
				}
			}
			if (!contains && new Date(parameter.get("date")).compareTo(Date.getCurrentDate()) >= 0) {
				sortedAdd(new Holiday(parameter, this));
			}
		}
	}
}
