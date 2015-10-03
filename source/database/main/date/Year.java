package database.main.date;

public class Year {
	public boolean	leapYear;
	public int		counter;
	public int		dayCount;
	public Month[]	months	= new Month[12];

	public Year(int counter) {
		this.counter = counter;
		if (((counter % 4 == 0) & !(counter % 100 == 0)) | (counter % 400 == 0)) {
			this.leapYear = true;
		}
		for (int x = 0; x < 12; x++) {
			months[x] = new Month(x + 1, this);
		}
		this.dayCount = getDayCount();
	}

	private int getDayCount() {
		int dayCount = 0;
		for (int i = 0; i < months.length; i++) {
			dayCount = months[i].dayCount + dayCount;
		}
		return dayCount;
	}

	public String toString() {
		return String.valueOf(counter);
	}
}