package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.services.ServiceRegistry;
import database.services.stringComplete.HashMapStringComplete;
import database.services.writerReader.IWriteRead;
import database.services.writerReader.IWriterReader;

public abstract class Plugin implements IWriteRead {
	public boolean			display;
	public String			identity;
	private IOutputHandler	outputHandler;

	public Plugin(String identity, IOutputHandler dataHandler) {
		this.identity = identity;
		outputHandler = dataHandler;
		display = true;
	}

	public void conduct(String command) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				if (method.getAnnotation(Command.class).tag().equals(command)) {
					method.invoke(this);
					return;
				}
			}
		}
	}

	@Command(tag = "display") public void display() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		HashMapStringComplete stringComplete = new HashMapStringComplete(RequestType.BOOLEAN);
		display = Boolean.valueOf(terminal.request("display", RequestType.BOOLEAN, stringComplete));
		terminal.update();
	}

	public String getCommandTags(Class<?> classWithMethods) {
		String regex = "(";
		ArrayList<String> strings = new ArrayList<>();
		for (Method method : classWithMethods.getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				strings.add(method.getAnnotation(Command.class).tag());
			}
		}
		Collections.sort(strings);
		for (String string : strings) {
			regex += string + "|";
		}
		return regex.endsWith("|") ? regex.substring(0, regex.lastIndexOf("|")) + ")" : "()";
	}

	public IOutputHandler getOutputHandler() {
		return outputHandler;
	}

	public void initialOutput() throws SQLException, BadLocationException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String initialOutput = outputHandler.getInitialOutput();
		if (!initialOutput.isEmpty()) {
			terminal.printLine(identity, StringType.MAIN, StringFormat.BOLD);
			terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void read(Node node) throws ParserConfigurationException, DOMException {
		if (node.getNodeName().equals("display")) {
			display = Boolean.valueOf(node.getTextContent());
		}
	}

	@Command(tag = "show") public void show()	throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException,
												InvocationTargetException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String regex = getCommandTags(outputHandler.getClass());
		String command = "";
		if (regex.split("\\|").length > 1) {
			command = terminal.request("show", regex, new HashMapStringComplete(regex));
		}
		for (Method method : outputHandler.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && (command.isEmpty() || method.getAnnotation(Command.class).tag().equals(command))) {
				Object output = method.invoke(outputHandler);
				terminal.printLine(output, StringType.REQUEST, StringFormat.STANDARD);
				terminal.waitForInput();
				break;
			}
		}
	}

	@Override public void write() {
		IWriterReader writerReader = ServiceRegistry.Instance().get(IWriterReader.class);
		writerReader.add(identity, "display", String.valueOf(display));
	}
}