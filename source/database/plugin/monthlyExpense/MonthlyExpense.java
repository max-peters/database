package database.plugin.monthlyExpense;

import java.time.LocalDate;
import org.w3c.dom.Element;
import database.plugin.expense.Expense;

public class MonthlyExpense extends Expense {
	public ExecutionDay executionDay;

	public MonthlyExpense(String name, String category, Double value, LocalDate date, ExecutionDay executionDay) {
		super(name, category, value, date);
		this.executionDay = executionDay;
	}

	@Override public void insertParameter(Element element) {
		super.insertParameter(element);
		element.setAttribute("executionday", executionDay.toString());
	}
}
