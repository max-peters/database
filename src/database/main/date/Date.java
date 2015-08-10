package database.main.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Date implements Comparable<Date> {
	public Day		day;
	public Month	month;
	public Year		year;

	public Date(String date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		String currentDate = format.format(calendar.getTime());
		String inputSplitResult[] = date.split(Pattern.quote("."));
		String dateSplitResult[] = currentDate.split(Pattern.quote("."));
		if ((date == null) || (date.length() == 0)) {
			year = new Year(Integer.parseInt(dateSplitResult[2]));
			month = year.months[Integer.parseInt(dateSplitResult[1]) - 1];
			day = year.months[Integer.parseInt(dateSplitResult[1]) - 1].days[Integer.parseInt(dateSplitResult[0]) - 1];
		}
		else {
			if (inputSplitResult.length == 3) {
				year = new Year(Integer.parseInt(inputSplitResult[2]));
				month = year.months[Integer.parseInt(inputSplitResult[1]) - 1];
				day = year.months[Integer.parseInt(inputSplitResult[1]) - 1].days[Integer.parseInt(inputSplitResult[0]) - 1];
			}
			else if (inputSplitResult.length == 2) {
				year = new Year(Integer.parseInt(dateSplitResult[2]));
				month = year.months[Integer.parseInt(inputSplitResult[1]) - 1];
				day = year.months[Integer.parseInt(inputSplitResult[1]) - 1].days[Integer.parseInt(inputSplitResult[0]) - 1];
			}
			else if (inputSplitResult.length == 1) {
				year = new Year(Integer.parseInt(dateSplitResult[2]));
				month = year.months[Integer.parseInt(dateSplitResult[1]) - 1];
				day = year.months[Integer.parseInt(inputSplitResult[1]) - 1].days[Integer.parseInt(inputSplitResult[0]) - 1];
			}
			else {
				throw new IllegalArgumentException("wrong input string for date instantiation: '" + date + "'");
			}
		}
	}

	public String toString() {
		return String.format("%2s", day.counter).replace(' ', '0') + "." + String.format("%2s", month.counter).replace(' ', '0') + "." + String.format("%4s", year.counter).replace(' ', '0');
	}

	public int compareTo(Date date) {
		return calculateDifference(date);
	}

	private int calculateDifference(Date date) {
		int difference;
		int sum = 0;
		for (int i = 0; i < date.month.counter - 1; i++) {
			for (int j = 0; j < date.year.months[i].days.length; j++) {
				sum++;
			}
		}
		for (int j = 0; j < date.day.counter; j++) {
			sum++;
		}
		for (int i = 1970; i < date.year.counter; i++) {
			Year currentYear = new Year(i);
			sum = currentYear.dayCount + sum;
		}
		int sum2 = 0;
		for (int i = 0; i < month.counter - 1; i++) {
			for (int j = 0; j < year.months[i].days.length; j++) {
				sum2++;
			}
		}
		for (int j = 0; j < day.counter; j++) {
			sum2++;
		}
		for (int i = 1970; i < year.counter; i++) {
			Year currentYear = new Year(i);
			sum2 = currentYear.dayCount + sum2;
		}
		difference = sum2 - sum;
		return difference;
	}

	public static Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		return new Date(format.format(calendar.getTime()));
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
}
