package database.main.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time {
	public static String getTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(calendar.getTime());
	}

	private int	hour;
	private int	minute;

	public Time(int hour, int minute) {
		if (hour >= 0 && hour <= 24 && minute >= 0 && minute <= 60) {
			this.hour = hour;
			this.minute = minute;
		}
	}

	@Override public String toString() {
		String stringRepresentation;
		if (hour < 10) {
			stringRepresentation = "0" + hour;
		}
		else {
			stringRepresentation = hour + "";
		}
		if (minute < 10) {
			stringRepresentation = stringRepresentation + ":0" + minute;
		}
		else {
			stringRepresentation = stringRepresentation + ":" + minute;
		}
		return stringRepresentation;
	}
}