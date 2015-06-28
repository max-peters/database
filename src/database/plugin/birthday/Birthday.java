package database.plugin.birthday;

import database.date.Date;
import database.main.Instance;

public class Birthday extends Instance {
	public Date		date;
	public String	name;

	public Birthday(String[] parameter, BirthdayList list) {
		super(parameter[0], list);
		this.name = parameter[0];
		this.date = new Date(parameter[1]);
	}

	public String toString() {
		return "birthday : " + name + " / " + date.toString();
	}

	private Birthday checkBirthday() {
		Date localDate = null;
		Date currentDate = Date.getDate();
		if (((currentDate.month.counter > date.month.counter) & (currentDate.day.counter > date.day.counter)) | (currentDate.month.counter > date.month.counter)) {
			int currentNextYear = currentDate.year.counter + 1;
			localDate = new Date(date.day.counter + "." + date.month.counter + "." + currentNextYear);
		}
		else {
			localDate = new Date(date.day.counter + "." + date.month.counter + "." + currentDate.year.counter);
		}
		if ((currentDate.calculateDifference(localDate) < 3) && (currentDate.calculateDifference(localDate) >= 0)) {
			String[] parameter = { name, localDate.toString() };
			Birthday localBirthday = new Birthday(parameter, (BirthdayList) list);
			return localBirthday;
		}
		else {
			return null;
		}
	}

	private int getAge() {
		return Date.getDate().year.counter - date.year.counter;
	}

	protected String singleOutput() {
		int nameLength = 0;
		int ageLength = 0;
		String toReturn = "";
		String newName = name;
		Birthday localBirthday;
		for (Object object : list.getList()) {
			Birthday birthday = (Birthday) object;
			if (birthday.checkBirthday() != null) {
				if (birthday.checkBirthday().name.length() > nameLength) {
					nameLength = birthday.checkBirthday().name.length();
				}
				if (String.valueOf(birthday.getAge()).length() > ageLength) {
					ageLength = String.valueOf(birthday.getAge()).length();
				}
			}
		}
		localBirthday = checkBirthday();
		for (newName.length(); newName.length() < nameLength + 1;) {
			newName = newName + " ";
		}
		if (localBirthday != null) {
			toReturn = toReturn + newName + " [" + String.format("%" + ageLength + "s", getAge()).replace(' ', '0') + "] - " + localBirthday.date.toString();
		}
		return toReturn;
	}
}
