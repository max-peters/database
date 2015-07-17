package database.main.date;

public class Month {
	public int		counter;
	public int		dayCount;
	public String	name;
	public Day[]	days;
	public Year		year;

	public Month(int counter, Year year) {
		if (counter == 1) {
			this.name = "January";
			this.dayCount = 31;
			this.counter = 1;
		}
		else if (counter == 2) {
			this.name = "February";
			this.counter = 2;
			if (year.leapYear) {
				this.dayCount = 29;
			}
			else {
				this.dayCount = 28;
			}
		}
		else if (counter == 3) {
			this.name = "March";
			this.dayCount = 31;
			this.counter = 3;
		}
		else if (counter == 4) {
			this.name = "April";
			this.dayCount = 30;
			this.counter = 4;
		}
		else if (counter == 5) {
			this.name = "May";
			this.dayCount = 31;
			this.counter = 5;
		}
		else if (counter == 6) {
			this.name = "June";
			this.dayCount = 30;
			this.counter = 6;
		}
		else if (counter == 7) {
			this.name = "July";
			this.dayCount = 31;
			this.counter = 7;
		}
		else if (counter == 8) {
			this.name = "August";
			this.dayCount = 31;
			this.counter = 8;
		}
		else if (counter == 9) {
			this.name = "September";
			this.dayCount = 30;
			this.counter = 9;
		}
		else if (counter == 10) {
			this.name = "October";
			this.dayCount = 31;
			this.counter = 10;
		}
		else if (counter == 11) {
			this.name = "November";
			this.dayCount = 30;
			this.counter = 11;
		}
		else if (counter == 12) {
			this.name = "December";
			this.dayCount = 31;
			this.counter = 12;
		}
		this.days = new Day[this.dayCount];
		for (int i = 0; i < this.dayCount; i++) {
			this.days[i] = new Day(i + 1);
		}
		this.year = year;
	}

	public boolean equals(Object object) {
		Month month;
		boolean equal = false;
		if (!object.getClass().equals(Month.class)) {
			equal = false;
		}
		month = (Month) object;
		if (month.counter == counter && month.year.counter == year.counter) {
			equal = true;
		}
		return equal;
	}

	public String toString() {
		return String.valueOf(counter);
	}
}