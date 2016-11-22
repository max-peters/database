package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.google.gson.Gson;
import database.main.PluginContainer;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;

public abstract class InstancePlugin<T extends Instance> extends Plugin {
	public Class<T>					type;
	protected OutputFormatter<T>	formatter;
	protected LinkedList<T>			list;

	public InstancePlugin(String identity, OutputFormatter<T> formatter, Class<T> type) {
		super(identity);
		this.list = new LinkedList<>();
		this.formatter = formatter;
		this.type = type;
	}

	public void add(T instance) {
		list.add(instance);
	}

	public void clearList() {
		list.clear();
	}

	public void createAndAdd(String json) {
		add(new Gson().fromJson(json, type));
	}

	public Iterable<T> getIterable() {
		return new LinkedList<>(list);
	}

	@Override public void initialOutput(ITerminal terminal, PluginContainer pluginContainer) throws BadLocationException {
		String initialOutput = formatter.getInitialOutput(getIterable());
		if (!initialOutput.isEmpty()) {
			terminal.printLine(identity, StringType.MAIN, StringFormat.BOLD);
			terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void print(Document document, Element appendTo) {
		for (T instance : list) {
			Element entryElement = document.createElement("entry");
			entryElement.setTextContent(instance.toString());
			appendTo.appendChild(entryElement);
		}
		Element entryElement = document.createElement("display");
		entryElement.setTextContent(String.valueOf(display));
		appendTo.appendChild(entryElement);
	}

	@Override public void read(Node node) throws ParserConfigurationException, DOMException {
		if (node.getNodeName().equals("entry")) {
			createAndAdd(node.getTextContent());
		}
		else if (node.getNodeName().equals("display")) {
			display = Boolean.valueOf(node.getTextContent());
		}
	}

	public void remove(Instance toRemove) throws BadLocationException, InterruptedException {
		list.remove(toRemove);
	}

	@Command(tag = "show") public void show(ITerminal terminal)	throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException,
																InvocationTargetException, UserCancelException {
		String command = terminal.request("show", getCommandTags(formatter.getClass()));
		for (Method method : formatter.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				List<Object> parameter = new ArrayList<>();
				parameter.add(getIterable());
				for (Parameter p : method.getParameters()) {
					if (p.getType().equals(ITerminal.class)) {
						parameter.add(terminal);
					}
				}
				Object output = method.invoke(formatter, parameter.toArray());
				terminal.getLineOfCharacters('-', StringType.REQUEST);
				terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				terminal.waitForInput();
			}
		}
	}

	@Command(tag = "store") public void store(PluginContainer pluginContainer, ITerminal terminal) throws BadLocationException, InterruptedException, UserCancelException {
		if (Boolean.valueOf(terminal.request("do you want to store all entries", "(true|false)"))) {
			pluginContainer.getStorage().store(this, terminal, pluginContainer);
		}
	}
}
