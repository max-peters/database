package database.main.date;

public class Month {
	public int		counter;
	public Day[]	days;
	public Year		year;

	public Month(int counter, Year year) {
		int dayCount;
		if (counter == 1) {
			dayCount = 31;
			this.counter = 1;
		}
		else if (counter == 2) {
			this.counter = 2;
			if (year.leapYear) {
				dayCount = 29;
			}
			else {
				dayCount = 28;
			}
		}
		else if (counter == 3) {
			dayCount = 31;
			this.counter = 3;
		}
		else if (counter == 4) {
			dayCount = 30;
			this.counter = 4;
		}
		else if (counter == 5) {
			dayCount = 31;
			this.counter = 5;
		}
		else if (counter == 6) {
			dayCount = 30;
			this.counter = 6;
		}
		else if (counter == 7) {
			dayCount = 31;
			this.counter = 7;
		}
		else if (counter == 8) {
			dayCount = 31;
			this.counter = 8;
		}
		else if (counter == 9) {
			dayCount = 30;
			this.counter = 9;
		}
		else if (counter == 10) {
			dayCount = 31;
			this.counter = 10;
		}
		else if (counter == 11) {
			dayCount = 30;
			this.counter = 11;
		}
		else if (counter == 12) {
			dayCount = 31;
			this.counter = 12;
		}
		else {
			throw new IllegalArgumentException();
		}
		days = new Day[dayCount];
		for (int i = 0; i < dayCount; i++) {
			days[i] = new Day(i + 1);
		}
		this.year = year;
	}

	@Override public boolean equals(Object object) {
		if (object == null || !object.getClass().equals(Month.class)) {
			return false;
		}
		Month month = (Month) object;
		if (month.counter == counter && month.year.counter == year.counter) {
			return true;
		}
		else {
			return false;
		}
	}

	public int getDayCount() {
		return days.length;
	}

	@Override public String toString() {
		return String.valueOf(counter);
	}
}