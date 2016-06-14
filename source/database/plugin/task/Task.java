package database.plugin.task;

import java.time.LocalDate;
import database.plugin.Instance;

public class Task extends Instance {
	public String		category;
	public String		name;
	public LocalDate	date;

	public Task(String name, String category, LocalDate date) {
		this.name = name;
		this.category = category;
		this.date = date;
	}
}
