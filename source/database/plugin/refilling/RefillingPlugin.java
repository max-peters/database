package database.plugin.refilling;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.text.BadLocationException;

import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.expense.Expense;
import database.services.ServiceRegistry;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.DependingInsertCommand;

public class RefillingPlugin extends Plugin {
	public RefillingPlugin() {
		super("refilling", new RefillingOutputHandler());
	}

	@Command(tag = "new")
	public void createRequest() throws InterruptedException, BadLocationException, NumberFormatException,
			UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		Double distance = Double.valueOf(terminal.request("enter driven distance", RequestType.DOUBLE));
		Double refuelAmount = Double.valueOf(terminal.request("enter refuel amount", RequestType.DOUBLE));
		Double cost = Double.valueOf(terminal.request("enter cost", RequestType.DOUBLE));
		String temp = terminal.request("enter date", RequestType.DATE,
				LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		LocalDate date = temp.isEmpty() ? LocalDate.now()
				: LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Refilling refilling = new Refilling(distance, refuelAmount, cost, date);
		Expense expense = new Expense("Tankstelle", "Fahrtkosten", cost, date);
		CommandHandler.Instance().executeCommand(new DependingInsertCommand(refilling, expense));
	}
}
