package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.google.gson.Gson;
import database.main.PluginContainer;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.ITerminal;

public abstract class InstancePlugin<T extends Instance> extends Plugin {
	protected LinkedList<T>	list;
	public Class<T>			instanceClass;

	public InstancePlugin(String identity, Class<T> instanceClass) {
		super(identity);
		this.list = new LinkedList<T>();
		this.instanceClass = instanceClass;
	}

	public void add(T instance) {
		list.add(instance);
	}

	public void clearList() {
		list.clear();
	}

	public void createAndAdd(String json) {
		add(new Gson().fromJson(json, instanceClass));
	}

	public Iterable<T> getIterable() {
		return new LinkedList<T>(list);
	}

	@Override public void initialOutput(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException {
		OutputFormatter<T> formatter = (OutputFormatter<T>) formatterProvider.getFormatter(instanceClass);
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

	@Command(tag = "show") public void show(ITerminal terminal, FormatterProvider formatterProvider)	throws InterruptedException, BadLocationException, IllegalAccessException,
																									IllegalArgumentException, InvocationTargetException {
		OutputFormatter<T> formatter = (OutputFormatter<T>) formatterProvider.getFormatter(instanceClass);
		String command = terminal.request("show", getCommandTags(formatter.getClass()));
		for (Method method : formatter.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				Object output = method.invoke(formatter, getIterable());
				terminal.getLineOfCharacters('-', StringType.SOLUTION);
				terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				terminal.waitForInput();
			}
		}
	}

	@Command(tag = "store") public void store(PluginContainer pluginContainer, ITerminal terminal, FormatterProvider formatterProvider)	throws BadLocationException,
																																		InterruptedException {
		if (Boolean.valueOf(terminal.request("do you want to store all entries", "(true|false)"))) {
			pluginContainer.getStorage().store(this, terminal, pluginContainer, formatterProvider);
		}
	}
}
