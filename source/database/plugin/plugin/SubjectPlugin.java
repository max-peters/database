package database.plugin.plugin;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.autocompletition.HashMapAutocomplete;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.connector.SubjectDatabaseConnector;
import database.plugin.element.Subject;
import database.plugin.outputHandler.SubjectOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.ConnectorRegistry;
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
		Subject toChange = getSubject(terminal.request("add", regex, new HashMapAutocomplete(regex)));
		Double newScore = Double.parseDouble(terminal.request("score", "[0-9]{1,13}(\\.[0-9]*)?"));
		Double newMaxPoint = Double.parseDouble(terminal.request("maximum points", "[0-9]{1,13}(\\.[0-9]*)?"));
		Subject changed = new Subject(toChange.name, toChange.score + newScore, toChange.maxPoints + newMaxPoint, toChange.counter + 1);
		CommandHandler.Instance().executeCommand(new ChangeCommand(toChange, changed));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		Subject subject = new Subject(terminal.request("name", "[A-ZÖÄÜ].*"), 0.0, 0.0, 0);
		CommandHandler.Instance().executeCommand(new InsertCommand(subject));
	}

	private Subject getSubject(String name) throws SQLException {
		ConnectorRegistry registry = ServiceRegistry.Instance().get(ConnectorRegistry.class);
		SubjectDatabaseConnector subjectConnector = (SubjectDatabaseConnector) registry.get(Subject.class);
		Subject wanted = null;
		for (Subject subject : subjectConnector.getList()) {
			if (subject.name.equalsIgnoreCase(name)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	private String getSubjectsAsRegex() throws SQLException {
		ConnectorRegistry registry = ServiceRegistry.Instance().get(ConnectorRegistry.class);
		SubjectDatabaseConnector subjectConnector = (SubjectDatabaseConnector) registry.get(Subject.class);
		String regex = "(";
		for (Subject subject : subjectConnector.getList()) {
			char c[] = subject.name.toCharArray();
			c[0] = Character.toLowerCase(c[0]);
			regex += subject.name + "|";
			regex += new String(c) + "|";
		}
		return regex.substring(0, regex.lastIndexOf("|")) + ")";
	}
}
