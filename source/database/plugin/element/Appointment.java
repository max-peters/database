package database.plugin.element;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Appointment extends CalendarElement {
	public LocalDate	beginDate;
	public LocalTime	beginTime;
	public int			daysTilRepeat;
	public LocalDate	endDate;
	public LocalTime	endTime;
	public String		name;

	public Appointment(String name, LocalDate beginDate, LocalTime beginTime, LocalDate endDate, LocalTime endTime, int daysTilRepeat) {
		this.name = name;
		this.beginDate = beginDate;
		this.beginTime = beginTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.daysTilRepeat = daysTilRepeat;
	}

	@Override public String getAdditionToOutput() {
		String string = "";
		if (beginTime != null) {
			string = "[" + beginTime + " UHR";
			if (beginDate.isEqual(endDate)) {
				if (endTime != null) {
					string += " to " + endTime + " UHR]";
				}
				else {
					string += "]";
				}
			}
			else {
				string += " until " + endDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
				if (endTime != null) {
					string += " " + endTime + " UHR]";
				}
				else {
					string += "]";
				}
			}
		}
		return string;
	}

	@Override public LocalDate getDate() {
		return beginDate;
	}

	@Override public String getName() {
		return name;
	}

	@Override public boolean isPast() {
		if (beginTime != null && endTime == null && beginDate.isEqual(endDate) && LocalDateTime.now().isAfter(getBeginDateTime())) {
			return true;
		}
		else if (LocalDateTime.now().isAfter(getEndDateTime())) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isRepeatable() {
		return daysTilRepeat != 0;
	}

	@Override public LocalDateTime orderDate() {
		return getBeginDateTime().withHour(0).withMinute(0);
	}

	public Appointment repeat() {
		return new Appointment(name, beginDate.plusDays(daysTilRepeat), beginTime, endDate.plusDays(daysTilRepeat), endTime, daysTilRepeat);
	}

	@Override public LocalDate updateYear() {
		return beginDate;
	}

	private LocalDateTime getBeginDateTime() {
		LocalDateTime dateTime;
		if (beginTime != null) {
			dateTime = beginDate.atTime(beginTime);
		}
		else {
			dateTime = beginDate.atTime(23, 59);
		}
		return dateTime;
	}

	private LocalDateTime getEndDateTime() {
		LocalDateTime dateTime;
		if (endTime != null) {
			dateTime = endDate.atTime(endTime);
		}
		else {
			dateTime = endDate.atTime(23, 59);
		}
		return dateTime;
	}
}
