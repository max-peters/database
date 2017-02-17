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
	private LocalDate	beginDate;
	private LocalTime	beginTime;
	private int			daysTilRepeat;
	private LocalDate	endDate;
	private LocalTime	endTime;
	private String		name;
	private String		spezification;

	public Appointment(String name, LocalDate beginDate, LocalTime beginTime, LocalDate endDate, LocalTime endTime, int daysTilRepeat, String spezification) {
		this.name = name;
		this.beginDate = beginDate;
		this.beginTime = beginTime;
		this.endDate = endDate;
		this.endTime = endTime;
		this.daysTilRepeat = daysTilRepeat;
		this.spezification = spezification;
	}

	public String getSpezification() {
		return spezification;
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

	public LocalTime getBeginTime() {
		return beginTime;
	}

	@Override public LocalDate getDate() {
		return beginDate;
	}

	public int getDaysTilRepeat() {
		return daysTilRepeat;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	@Override public String getName() {
		return name;
	}

	@Override public int getPriority() {
		return ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters().getCalendarElementPriority(CalendarElements.APPOINTMENT);
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
		return new Appointment(name, beginDate.plusDays(daysTilRepeat), beginTime, endDate.plusDays(daysTilRepeat), endTime, daysTilRepeat, spezification);
	}

	@Override public LocalDate updateYear() {
		return beginDate;
	}
}
