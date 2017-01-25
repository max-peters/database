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
		this.endTime = endTime.equals(LocalTime.of(23, 59, 59)) ? LocalTime.MAX : endTime;
		this.daysTilRepeat = daysTilRepeat;
	}

	@Override public String getAdditionToOutput() {
		String string = "";
		if (!beginTime.equals(LocalTime.MIN)) {
			string = "[" + beginTime + " UHR";
			if (beginDate.isEqual(endDate)) {
				if (!endTime.equals(LocalTime.MAX)) {
					string += " to " + endTime + " UHR]";
				}
				else {
					string += "]";
				}
			}
			else {
				string += " until " + endDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
				if (!endTime.equals(LocalTime.MAX)) {
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
		if (!beginTime.equals(LocalTime.MIN) && endTime.equals(LocalTime.MAX) && beginDate.isEqual(endDate) && LocalDateTime.now().isAfter(beginDate.atTime(beginTime))) {
			return true;
		}
		else if (LocalDateTime.now().isAfter(endDate.atTime(endTime))) {
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
		return beginDate.atTime(beginTime);
	}

	public Appointment repeat() {
		return new Appointment(name, beginDate.plusDays(daysTilRepeat), beginTime, endDate.plusDays(daysTilRepeat), endTime, daysTilRepeat);
	}

	@Override public LocalDate updateYear() {
		return beginDate;
	}
}
