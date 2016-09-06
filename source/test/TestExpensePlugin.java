package test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import database.plugin.expense.ExpenseOutputFormatter;
import database.plugin.expense.ExpensePlugin;

public class TestExpensePlugin {
	TestTerminal			terminal			= new TestTerminal();
	ExpensePlugin			expensePlugin		= new ExpensePlugin();
	ExpenseOutputFormatter	expenseFormatter	= new ExpenseOutputFormatter();

	@Before public void setUp() {
		terminal = new TestTerminal();
		expensePlugin = new ExpensePlugin();
	}

	@After public void tearDown() {
		terminal = null;
		expensePlugin = null;
	}

	@Test public void NoEntriesTest() {
		Assert.assertEquals(expenseFormatter.outputAverage(expensePlugin.getIterable()), "no entries");
		Assert.assertEquals(expenseFormatter.outputCategory(expensePlugin.getIterable()), "no entries");
		Assert.assertEquals(expenseFormatter.outputMonth(expensePlugin.getIterable()), "no entries");
		Assert.assertEquals(expenseFormatter.printAll(expensePlugin.getIterable()), "no entries");
		Assert.assertEquals(expenseFormatter.printCurrent(expensePlugin.getIterable()), "no entries");
	}
}
