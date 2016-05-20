package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.google.gson.Gson;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;

public abstract class InstancePlugin<T extends Instance> extends Plugin {
	protected OutputFormatter<T>	formatter;
	protected LinkedList<T>			list;
	private Storage					storage;

	public InstancePlugin(String identity, Storage storage, OutputFormatter<T> formatter, Backup backup) {
		super(identity, backup);
		this.list = new LinkedList<T>();
		this.storage = storage;
		this.formatter = formatter;
	}

	public void add(T instance) {
		list.add(instance);
	}

	public void clearList() {
		list.clear();
	}

	public Iterable<T> getIterable() {
		return list;
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = formatter.getInitialOutput(getIterable());
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(getIdentity(), StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void print(Document document, Element appendTo) {
		for (T instance : list) {
			Element entryElement = document.createElement("entry");
			entryElement.setTextContent(instance.toString());
			appendTo.appendChild(entryElement);
		}
		Element entryElement = document.createElement("display");
		entryElement.setTextContent(String.valueOf(getDisplay()));
		appendTo.appendChild(entryElement);
	}

	@Override public void read(Node node) throws ParserConfigurationException, DOMException {
		@SuppressWarnings("unchecked") Class<T> c = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (node.getNodeName().equals("entry")) {
			add(new Gson().fromJson(node.getTextContent(), c));
		}
		else if (node.getNodeName().equals("display")) {
			setDisplay(Boolean.valueOf(node.getTextContent()));
		}
	}

	public void remove(Instance toRemove) throws BadLocationException, InterruptedException {
		if (list.remove(toRemove)) {
			Terminal.update();
		}
	}

	@Command(tag = "show") public void show() throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String command = Terminal.request("show", getCommandTags(formatter.getClass()));
		for (Method method : formatter.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				Object output = method.invoke(formatter, getIterable());
				Terminal.getLineOfCharacters('-');
				Terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				Terminal.waitForInput();
			}
		}
	}

	@Command(tag = "store") public void store() throws BadLocationException, InterruptedException {
		backup.backup();
		storage.store(this);
	}
}
