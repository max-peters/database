package database.plugin.subject;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.stringComplete.HashMapStringComplete;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.ChangeCommand;
import database.services.undoRedo.command.InsertCommand;

public class SubjectPlugin extends Plugin {
	public SubjectPlugin() {
		super("subject", new SubjectOutputHandler());
	}

	@Command(tag = "add") public void addRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String regex = getSubjectsAsRegex();
		Subject toChange = getSubject(terminal.request("enter subject", regex, new HashMapStringComplete(regex)));
		Double newScore = Double.parseDouble(terminal.request("enter score", RequestType.DOUBLE));
		Double newMaxPoint = Double.parseDouble(terminal.request("enter maximum points", RequestType.DOUBLE));
		Subject changed = new Subject(toChange.getName(), toChange.getScore() + newScore, toChange.getMaxPoints() + newMaxPoint, toChange.getCounter() + 1);
		CommandHandler.Instance().executeCommand(new ChangeCommand(toChange, changed));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		Subject subject = new Subject(terminal.request("enter subject name", RequestType.NAME), 0.0, 0.0, 0);
		CommandHandler.Instance().executeCommand(new InsertCommand(subject));
	}

	private Subject getSubject(String name) throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		SubjectDatabaseConnector subjectConnector = (SubjectDatabaseConnector) registry.get(Subject.class);
		Subject wanted = null;
		for (Subject subject : subjectConnector.getList()) {
			if (subject.getName().equalsIgnoreCase(name)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	private String getSubjectsAsRegex() throws SQLException {
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		SubjectDatabaseConnector subjectConnector = (SubjectDatabaseConnector) registry.get(Subject.class);
		String regex = "(";
		for (Subject subject : subjectConnector.getList()) {
			char c[] = subject.getName().toCharArray();
			c[0] = Character.toLowerCase(c[0]);
			regex += subject.getName() + "|";
			regex += new String(c) + "|";
		}
		return regex.substring(0, regex.lastIndexOf("|")) + ")";
	}
}
