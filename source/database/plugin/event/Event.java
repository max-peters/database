package database.plugin.event;

import java.time.LocalDate;
import database.plugin.Instance;

public abstract class Event extends Instance {
	public LocalDate	date;
	public String		name;

	public Event(String name, LocalDate date) {
		this.name = name;
		this.date = date;
	}

	public abstract LocalDate updateYear();

	protected abstract String getAdditionToOutput();
}
