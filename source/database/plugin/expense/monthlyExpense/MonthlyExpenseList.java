package database.plugin.expense.monthlyExpense;

import java.util.Map;

import database.plugin.InstanceList;
import database.plugin.expense.ExpenseList;

public class MonthlyExpenseList extends InstanceList {
	private ExpenseList	expenseList;

	public MonthlyExpenseList(ExpenseList expenseList) {
		super();
		this.expenseList = expenseList;
	}

	@Override public void add(Map<String, String> parameter) {
		getList().add(new MonthlyExpense(parameter, this, expenseList));
	}
}
