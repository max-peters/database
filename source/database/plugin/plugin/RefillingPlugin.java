package database.plugin.plugin;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.element.Expense;
import database.plugin.element.Refilling;
import database.plugin.outputHandler.RefillingOutputHandler;
import database.services.ServiceRegistry;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.DependingInsertCommand;

public class RefillingPlugin extends Plugin {
	public RefillingPlugin() {
		super("refilling", new RefillingOutputHandler());
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, NumberFormatException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		Double distance = Double.valueOf(terminal.request("distance", "[0-9]{1,13}(\\.[0-9]*)?"));
		Double refuelAmount = Double.valueOf(terminal.request("refuelAmount", "[0-9]{1,13}(\\.[0-9]*)?"));
		Double cost = Double.valueOf(terminal.request("cost", "[0-9]{1,13}(\\.[0-9]*)?"));
		String temp = terminal.request("date", "DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		LocalDate date = temp.isEmpty() ? LocalDate.now() : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		Refilling refilling = new Refilling(distance, refuelAmount, cost, date);
		Expense expense = new Expense("Tankstelle", "Fahrtkosten", refilling.cost, refilling.date);
		CommandHandler.Instance().executeCommand(new DependingInsertCommand(refilling, expense));
	}
}