package database.plugin.expense;

import java.util.Map;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.main.date.Date;
import database.plugin.Instance;

public class Expense extends Instance {
	public Expense(Map<String, String> parameter, ExpenseList list) {
		super(parameter, "expense", list);
	}

	@Getter Date getDate() {
		return new Date(getParameter("date"));
	}

	@Getter String getName() {
		return getParameter("name");
	}

	@Getter String getCategory() {
		return getParameter("category");
	}

	@Getter Double getValue() {
		return Double.valueOf(getParameter("value"));
	}

	@Override public int compareTo(Instance instance) {
		if (instance instanceof Expense) {
			return getDate().compareTo(((Expense) instance).getDate());
		}
		else {
			return 0;
		}
	}

	protected boolean checkValidity(int month, int year) {
		if ((getDate().month.counter == month && getDate().year.counter == year) || (month < 0 && year < 0)) {
			return true;
		}
		return false;
	}
}