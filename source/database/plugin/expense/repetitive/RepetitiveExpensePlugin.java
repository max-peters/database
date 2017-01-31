package database.plugin.expense.repetitive;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.main.userInterface.StringFormat;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.expense.Expense;
import database.plugin.expense.ExpenseDatabaseConnector;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.database.IDatabase;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.DeleteCommand;
import database.services.undoRedo.command.DependingInsertCommand;

public class RepetitiveExpensePlugin extends Plugin {
	public RepetitiveExpensePlugin() {
		super("repetitiveexpense", new RepetitiveExpenseOutputHandler());
	}

	public void createExpense() throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		RepetitiveExpenseDatabaseConnector repetitiveExpenseConnector = (RepetitiveExpenseDatabaseConnector) registry.get(RepetitiveExpense.class);
		List<Expense> newExpenses = new LinkedList<>();
		for (RepetitiveExpense repetitiveExpense : repetitiveExpenseConnector.getList()) {
			newExpenses.addAll(createExpense(repetitiveExpense));
		}
		for (Expense expense : newExpenses) {
			database.insert(expense);
			String printOut = " + "	+ expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " + expense.name + " (" + expense.category + ") " + expense.value
								+ "€";
			terminal.collectLine(printOut, StringFormat.STANDARD, "expense");
		}
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, NumberFormatException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		DependingInsertCommand command;
		String name = terminal.request("enter expense name", RequestType.NAME);
		String category = terminal.request("enter category", RequestType.NAME);
		Double value = Double.valueOf(terminal.request("enter value", RequestType.DOUBLE));
		String temp = terminal.request("enter date", RequestType.DATE);
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		ExecutionDay executionDay = ExecutionDay.getExecutionDay(terminal.request("enter day of execution", RequestType.EXECUTIONDAY));
		int interval = Integer.valueOf(terminal.request("enter repeat period in months", RequestType.INTEGER));
		RepetitiveExpense repetitiveExpense = new RepetitiveExpense(name, category, value, date, executionDay, interval);
		command = new DependingInsertCommand(repetitiveExpense);
		for (Expense expense : createExpense(repetitiveExpense)) {
			command.addDependingInstances(expense);
			String printOut = " + "	+ expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " " + expense.name + " (" + expense.category + ") " + expense.value
								+ "€";
			terminal.collectLine(printOut, StringFormat.STANDARD, "expense");
		}
		CommandHandler.Instance().executeCommand(command);
	}

	@Command(tag = "stop") public void stopRequest() throws InterruptedException, BadLocationException, SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		RepetitiveExpenseDatabaseConnector repetitiveExpenseConnector = (RepetitiveExpenseDatabaseConnector) registry.get(RepetitiveExpense.class);
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		List<String> strings = new ArrayList<>();
		List<RepetitiveExpense> list = repetitiveExpenseConnector.getList();
		for (Expense expense : list) {
			strings.add(expense.name + " - " + expense.category);
		}
		int position = terminal.checkRequest(strings, "choose expense to stop");
		if (position < 0) {
			return;
		}
		CommandHandler.Instance().executeCommand(new DeleteCommand(list.get(position)));
	}

	private LocalDate adjustDate(int month, int year, ExecutionDay executionDay) {
		LocalDate date = LocalDate.of(year, month, 1);
		switch (executionDay) {
			case FIRST:
				break;
			case LAST:
				date = date.withDayOfMonth(date.lengthOfMonth());
				break;
			case MID:
				date = date.withDayOfMonth(date.lengthOfMonth() / 2);
				break;
		}
		return date;
	}

	private List<Expense> createExpense(RepetitiveExpense repetitiveExpense) throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		ExpenseDatabaseConnector connector = (ExpenseDatabaseConnector) registry.get(Expense.class);
		List<Expense> list = new LinkedList<>();
		Expense expense = new Expense(repetitiveExpense.name, repetitiveExpense.category, repetitiveExpense.value, repetitiveExpense.date);
		expense.date = adjustDate(expense.date.getMonthValue(), expense.date.getYear(), repetitiveExpense.executionDay);
		while (expense.date.isBefore(LocalDate.now()) || expense.date.isEqual(LocalDate.now())) {
			if (!connector.contains(expense)	&& !list.contains(expense)
				&& Math.floorMod(ChronoUnit.MONTHS.between(repetitiveExpense.date, expense.date), repetitiveExpense.interval) == 0) {
				list.add(new Expense(expense.name, expense.category, expense.value, expense.date));
			}
			if (expense.date.getMonthValue() == 12) {
				expense.date = adjustDate(1, expense.date.getYear() + 1, repetitiveExpense.executionDay);
			}
			else {
				expense.date = adjustDate(expense.date.getMonthValue() + 1, expense.date.getYear(), repetitiveExpense.executionDay);
			}
		}
		return list;
	}
}