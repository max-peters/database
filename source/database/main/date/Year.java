package database.main.date;

public class Year {
	public int		counter;
	public boolean	leapYear;
	public Month[]	months;

	public Year(int counter) {
		this.counter = counter;
		months = new Month[12];
		if (counter % 4 == 0 & !(counter % 100 == 0) | counter % 400 == 0) {
			leapYear = true;
		}
		for (int x = 0; x < 12; x++) {
			months[x] = new Month(x + 1, this);
		}
	}

	public int getDayCount() {
		int dayCount = 0;
		for (int i = 0; i < months.length; i++) {
			dayCount = months[i].getDayCount() + dayCount;
		}
		return dayCount;
	}

	@Override public String toString() {
		return String.valueOf(counter);
	}
}