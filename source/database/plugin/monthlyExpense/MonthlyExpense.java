package database.plugin.monthlyExpense;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import database.main.date.Date;
import database.plugin.expense.Expense;

public class MonthlyExpense extends Expense {
	public ExecutionDay executionDay;

	public MonthlyExpense(String name, String category, Double value, Date date, ExecutionDay executionDay) {
		super(name, category, value, date);
		this.executionDay = executionDay;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.putAll(super.getParameter());
		parameter.put("executionday", executionDay.toString());
		return parameter;
	}

	@Override public void insertParameter(Element element) {
		super.insertParameter(element);
		element.setAttribute("executionday", executionDay.toString());
	}
}
