package database.main.date;

public class Time implements Comparable<Time> {
	private int	hour;
	private int	minute;

	public Time(String string) {
		String[] splitResult = string.split(":");
		int localHour = 0;
		int localMinute = 0;
		if (splitResult.length == 1 && !string.isEmpty()) {
			localHour = Integer.valueOf(splitResult[0]);
		}
		else if (splitResult.length == 2) {
			localHour = Integer.valueOf(splitResult[0]);
			localMinute = Integer.valueOf(splitResult[1]);
		}
		else {
			throw new IllegalArgumentException();
		}
		if (localHour >= 0 && localHour < 24 && localMinute >= 00 && localMinute < 60) {
			hour = localHour;
			minute = localMinute;
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	@Override public int compareTo(Time time) {
		int firstValue = hour * 60 + minute;
		int secondValue = time.hour * 60 + minute;
		return secondValue - firstValue;
	}

	@Override public String toString() {
		return (String.valueOf(hour).length() == 1 ? "0" + hour : hour) + ":" + (String.valueOf(minute).length() == 1 ? "0" + minute : minute);
	}

	public static boolean testTimeString(String timeInformation) {
		try {
			new Time(timeInformation);
			return true;
		}
		catch (Throwable e) {
			return false;
		}
	}
}
