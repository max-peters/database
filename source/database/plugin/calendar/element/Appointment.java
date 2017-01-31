package database.plugin.calendar.element;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import database.plugin.calendar.CalendarElements;
import database.services.ServiceRegistry;
import database.services.settings.ISettingsProvider;
import database.services.stringUtility.Builder;

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
		Builder builder = new Builder();
		builder.append("[");
		if (!beginTime.equals(LocalTime.MIN)) {
			builder.append(beginTime + "UHR");
		}
		if (!beginDate.isEqual(endDate)) {
			builder.append("until" + endDate.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
		}
		if (!endTime.equals(LocalTime.MIN)) {
			builder.append("to" + endTime + "UHR");
		}
		builder.append("]");
		return builder.build().replace("[]", "").replaceAll("(?<=[A-Z])(?=[a-z])|(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])", " ");
	}

	@Override public LocalDate getDate() {
		return beginDate;
	}

	@Override public String getName() {
		return name;
	}

	@Override public boolean isPast() {
		if (endTime.equals(LocalTime.MIN)) {
			if (beginTime.equals(LocalTime.MIN) || !beginDate.isEqual(endDate)) {
				return LocalDate.now().isAfter(endDate);
			}
			else {
				return LocalDateTime.now().isAfter(beginDate.atTime(beginTime));
			}
		}
		else {
			return LocalDateTime.now().isAfter(endDate.atTime(endTime));
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

	@Override public int getPriority() {
		return ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters().getCalendarElementPriority(CalendarElements.APPOINTMENT);
	}
}
