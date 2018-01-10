package database.plugin.expense;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.settings.ISettingsProvider;
import database.services.settings.InternalParameters;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.InsertCommand;

public class ExpensePlugin extends Plugin {
	public ExpensePlugin() throws SQLException {
		super("expense", new ExpenseOutputHandler());
	}

	@Command(tag = "add")
	public void addRequest() throws NumberFormatException, InterruptedException, BadLocationException,
			UserCancelException, SQLException, ParserConfigurationException, TransformerException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		InternalParameters settings = ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters();
		ExpenseDatabaseConnector connector = (ExpenseDatabaseConnector) registry.get(Expense.class);
		connector.addConvertedAmount(settings.getCurrentCurrency(),
				Double.valueOf(terminal.request("enter amount to add", RequestType.DOUBLE)));
	}

	@Command(tag = "currency")
	public void changeCurrencyRequest() throws InterruptedException, BadLocationException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		InternalParameters settings = ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters();
		settings.setCurrentCurrency(settings.getCurrencies().get(terminal.checkRequest(settings.getCurrencies(),
				settings.getCurrencies().indexOf(settings.getCurrentCurrency()), "choose default currency")));
	}

	@Command(tag = "new")
	public void createRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		ExpenseDatabaseConnector connector = (ExpenseDatabaseConnector) registry.get(Expense.class);
		InternalParameters settings = ServiceRegistry.Instance().get(ISettingsProvider.class).getInternalParameters();
		String requestResult;
		String name;
		String category;
		Double value;
		LocalDate date;
		requestResult = terminal.request("enter expense name", RequestType.NAME, connector.nameStringComplete);
		name = connector.nameStringComplete.getCorrespondingString(requestResult);
		requestResult = terminal.request("enter category", RequestType.NAME,
				connector.categoryStringComplete.getMostUsedString("", name), connector.categoryStringComplete);
		category = connector.categoryStringComplete.getCorrespondingString(requestResult);
		value = Double.valueOf(terminal.request("enter value", RequestType.DOUBLE));
		requestResult = terminal.request("enter date", "DATE",
				LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		date = requestResult.isEmpty() ? LocalDate.now()
				: LocalDate.parse(requestResult, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		CommandHandler.Instance().executeCommand(
				new InsertCommand(new Expense(name, category, value, date, settings.getCurrentCurrency())));
		connector.refreshStringComplete();
	}

	@Override
	public ExpenseOutputHandler getOutputHandler() {
		return (ExpenseOutputHandler) super.getOutputHandler();
	}
}
