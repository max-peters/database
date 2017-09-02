package database.plugin.expense;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.InsertCommand;
import database.services.writerReader.IWriterReader;

public class ExpensePlugin extends Plugin {
	private Currency currency;
	private Map<String, Double> defaultExchangeRates;

	public ExpensePlugin() throws SQLException {
		super("expense", new ExpenseOutputHandler());
		defaultExchangeRates = new HashMap<String, Double>();
		for (Currency currency : Currency.values()) {
			defaultExchangeRates.put(currency.toString(), 1.0);
		}
		currency = Currency.EUR;
	}

	@Command(tag = "new")
	public void createRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		ExpenseDatabaseConnector connector = (ExpenseDatabaseConnector) registry.get(Expense.class);
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
		Expense expense = new Expense(name, category, value, date);
		expense.setCurrency(currency);
		expense.setExchangeRate(defaultExchangeRates.get(currency.toString()));
		CommandHandler.Instance().executeCommand(new InsertCommand(expense));
		connector.refreshStringComplete();
	}

	@Override
	public ExpenseOutputHandler getOutputHandler() {
		return (ExpenseOutputHandler) super.getOutputHandler();
	}

	@Override
	public void write() {
		IWriterReader writerReader = ServiceRegistry.Instance().get(IWriterReader.class);
		writerReader.add(identity, "currency", currency.toString());
		for (Entry<String, Double> entry : defaultExchangeRates.entrySet()) {
			writerReader.add(identity, entry.getKey(), String.valueOf(entry.getValue()));
		}
	}

	@Override
	public void read(Node node) throws ParserConfigurationException, DOMException {
		if (node.getNodeName().equals("currency")) {
			currency = Currency.valueOf(node.getTextContent());
		}
		if (defaultExchangeRates.containsKey(node.getNodeName())) {
			defaultExchangeRates.put(node.getNodeName(), Double.valueOf(node.getTextContent()));
		}
	}
}
