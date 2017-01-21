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
import database.plugin.outputHandler.ExpenseOutputHandler;
import database.services.ServiceRegistry;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.InsertCommand;

public class ExpensePlugin extends Plugin {
	public ExpensePlugin() throws SQLException {
		super("expense", new ExpenseOutputHandler());
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String requestResult;
		String name;
		String category;
		Double value;
		LocalDate date;
		requestResult = terminal.request("name", "[A-ZÖÄÜa-zöäüß\\- ]+", getOutputHandler().nameAutocomplete);
		name = getOutputHandler().nameAutocomplete.getCorrespondingString(requestResult);
		requestResult = terminal.request("category", "[A-ZÖÄÜa-zöäüß\\- ]+", getOutputHandler().categoryAutocomplete.getMostUsedString("", name),
			getOutputHandler().categoryAutocomplete);
		category = getOutputHandler().categoryAutocomplete.getCorrespondingString(requestResult);
		value = Double.valueOf(terminal.request("value", "[0-9]{1,13}(\\.[0-9]{0,2})?"));
		requestResult = terminal.request("date", "DATE", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		date = requestResult.isEmpty() ? LocalDate.now() : LocalDate.parse(requestResult, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		CommandHandler.Instance().executeCommand(new InsertCommand(new Expense(name, category, value, date)));
	}

	@Override public ExpenseOutputHandler getOutputHandler() {
		return (ExpenseOutputHandler) super.getOutputHandler();
	}
}
