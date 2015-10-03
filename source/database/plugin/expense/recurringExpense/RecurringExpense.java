package database.plugin.expense.recurringExpense;

import java.util.Map;

import database.plugin.expense.Expense;
import database.plugin.expense.ExpenseList;

public class RecurringExpense extends Expense {
	public RecurringExpense(Map<String, String> parameter, ExpenseList list) {
		super(parameter, list);
		assert parameter.size() == 5;
		assert parameter.containsKey("intervall");
	}

	public void createExpense() {
	}
	// public ArrayList<Event> getNearEvents() {
	// Date currentDate = Date.getDate();
	// Date localDate;
	// ArrayList<Event> nearEvents = new ArrayList<Event>();
	// for (Object object : getList()) {
	// Event event = (Event) object;
	// localDate = event.updateYear();
	// if (localDate.compareTo(currentDate) < 5) {
	// nearEvents.add(event);
	// }
	// }
	// return nearEvents;
	// }
}
