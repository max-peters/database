package database.plugin.event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.w3c.dom.Element;
import database.plugin.Instance;

public abstract class Event extends Instance {
	public LocalDate	date;
	public String		name;

	public Event(String name, LocalDate date) {
		this.name = name;
		this.date = date;
	}

	@Override public boolean equals(Object object) {
		Event event;
		if (object != null && object.getClass().equals(this.getClass())) {
			event = (Event) object;
			if (event.date.isEqual(date) && event.name.equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override public void insertParameter(Element element) {
		element.setAttribute("name", name);
		element.setAttribute("date", date.format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
	}

	public LocalDate updateYear() {
		LocalDate currentDate = LocalDate.now();
		if (currentDate.getMonthValue() > date.getMonthValue() || currentDate.getMonthValue() == date.getMonthValue() && currentDate.getDayOfMonth() > date.getDayOfMonth()) {
			return date.withYear(currentDate.getYear() + 1);
		}
		else {
			return date.withYear(currentDate.getYear());
		}
	}

	protected abstract String getAdditionToOutput();
}
