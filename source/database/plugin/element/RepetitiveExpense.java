package database.plugin.element;

import java.time.LocalDate;
import database.plugin.ExecutionDay;

public class RepetitiveExpense extends Expense {
	public ExecutionDay	executionDay;
	public int			interval;

	public RepetitiveExpense(String name, String category, Double value, LocalDate date, ExecutionDay executionDay, int interval) {
		super(name, category, value, date);
		this.executionDay = executionDay;
		this.interval = interval;
	}
}
