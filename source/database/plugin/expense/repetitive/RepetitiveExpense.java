package database.plugin.expense.repetitive;

import java.time.LocalDate;

import database.plugin.expense.Expense;

public class RepetitiveExpense extends Expense {
	private ExecutionDay executionDay;
	private int interval;

	public RepetitiveExpense(String name, String category, Double value, LocalDate date, ExecutionDay executionDay,
			int interval) {
		super(name, category, value, date);
		this.executionDay = executionDay;
		this.interval = interval;
	}

	public ExecutionDay getExecutionDay() {
		return executionDay;
	}

	public int getInterval() {
		return interval;
	}
}
