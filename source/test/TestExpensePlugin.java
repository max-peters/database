package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import database.plugin.expense.ExpensePlugin;

public class TestExpensePlugin {
	TestTerminal	terminal		= new TestTerminal();
	ExpensePlugin	expensePlugin	= new ExpensePlugin();

	@Before public void setUp() {
		terminal = new TestTerminal();
		expensePlugin = new ExpensePlugin();
	}

	@After public void tearDown() {
		terminal = null;
		expensePlugin = null;
	}

	@Test public void test() {}
}
