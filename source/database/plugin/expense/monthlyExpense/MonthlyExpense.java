package database.plugin.expense.monthlyExpense;

import java.util.HashMap;
import java.util.Map;
import database.plugin.expense.Expense;

public class MonthlyExpense extends Expense {
	public ExecutionDay executionDay;

	public MonthlyExpense(Map<String, String> parameter) {
		super(parameter);
		executionDay = ExecutionDay.getExecutionDay(parameter.get("executionday"));
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.putAll(super.getParameter());
		parameter.put("executionday", executionDay.toString());
		return parameter;
	}
}
