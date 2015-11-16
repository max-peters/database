package database.main.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Date implements Comparable<Date> {
	public static Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		return new Date(format.format(calendar.getTime()));
	}

	public static String getDateAsString() {
		Calendar calendar = Calendar.getInstance();
		calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		return format.format(calendar.getTime());
	}

	public static boolean testDateString(String dateInformation) {
		try {
			new Date(dateInformation);
		}
		catch (Throwable e) {
			return false;
		}
		return true;
	}

	public Day		day;
	public Month	month;
	public Year		year;

	public Date(String date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		String currentDate = format.format(calendar.getTime());
		String dateSplitResult[] = currentDate.split(Pattern.quote("."));
		String inputSplitResult[] = date.split(Pattern.quote("."));
		int counter = 0;
		for (int i = 0; i < date.length(); i++) {
			if (String.valueOf(date.charAt(i)).matches(Pattern.quote("."))) {
				counter++;
			}
		}
		if (date.length() == 0) {
			year = new Year(Integer.parseInt(dateSplitResult[2]));
			month = year.months[Integer.parseInt(dateSplitResult[1]) - 1];
			day = month.days[Integer.parseInt(dateSplitResult[0]) - 1];
		}
		else if (counter == 2) {
			year = new Year(Integer.parseInt(inputSplitResult[2]));
			month = year.months[Integer.parseInt(inputSplitResult[1]) - 1];
			day = month.days[Integer.parseInt(inputSplitResult[0]) - 1];
		}
		else if (counter == 1) {
			year = new Year(Integer.parseInt(dateSplitResult[2]));
			month = year.months[Integer.parseInt(inputSplitResult[1]) - 1];
			day = month.days[Integer.parseInt(inputSplitResult[0]) - 1];
		}
		else if (counter == 0) {
			year = new Year(Integer.parseInt(dateSplitResult[2]));
			month = year.months[Integer.parseInt(dateSplitResult[1]) - 1];
			day = month.days[Integer.parseInt(inputSplitResult[0]) - 1];
		}
	}

	@Override public int compareTo(Date date) {
		int startYear = year.counter < date.year.counter ? year.counter : date.year.counter;
		return calculateDaySum(this, startYear) - calculateDaySum(date, startYear);
	}

	public boolean isPast() {
		return getCurrentDate().compareTo(this) > 0;
	}

	public boolean isToday() {
		return getCurrentDate().compareTo(this) == 0;
	}

	@Override public String toString() {
		return String.format("%2s", day.counter).replace(' ', '0') + "." + String.format("%2s", month.counter).replace(' ', '0') + "."
				+ String.format("%4s", year.counter).replace(' ', '0');
	}

	private int calculateDaySum(Date date, int startYear) {
		int sum = 0;
		for (int i = 0; i < date.month.counter - 1; i++) {
			for (int j = 0; j < date.year.months[i].getDayCount(); j++) {
				sum++;
			}
		}
		for (int j = 0; j < date.day.counter; j++) {
			sum++;
		}
		for (int i = startYear; i < date.year.counter; i++) {
			Year currentYear = new Year(i);
			sum = currentYear.getDayCount() + sum;
		}
		return sum;
	}
}
