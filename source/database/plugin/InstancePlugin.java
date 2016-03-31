package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.storage.Storage;

public abstract class InstancePlugin<T extends Instance> extends Plugin {
	protected LinkedList<T>			list;
	protected OutputFormatter<T>	formatter;
	private Storage					storage;

	public InstancePlugin(String identity, Storage storage, OutputFormatter<T> formatter) {
		super(identity);
		this.list = new LinkedList<T>();
		this.storage = storage;
		this.formatter = formatter;
	}

	public void clearList() {
		list.clear();
	}

	public Iterable<T> getIterable() {
		return list;
	}

	public abstract T create(Map<String, String> map);

	public void add(T instance) {
		list.add(instance);
	}

	public void createAndAdd(Map<String, String> map) {
		add(create(map));
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = formatter.getInitialOutput(list);
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(getIdentity() + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> printInformationList = new ArrayList<PrintInformation>();
		for (T instance : list) {
			printInformationList.add(new PrintInformation("entry", instance.getParameter()));
		}
		printInformationList.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return printInformationList;
	}

	@Override public void read(PrintInformation pair) {
		if (pair.getName().equals("entry")) {
			createAndAdd(pair.getMap());
		}
		else if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
	}

	public void remove(Instance toRemove) throws BadLocationException {
		list.remove(toRemove);
		update();
	}

	@Command(tag = "show") public void show() throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String command = Terminal.request("show", getCommandTags(formatter.getClass()));
		for (Method method : formatter.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				Object output = method.invoke(formatter, list);
				Terminal.getLineOfCharacters('-');
				Terminal.printLine(output, StringType.SOLUTION, StringFormat.STANDARD);
				Terminal.waitForInput();
			}
		}
	}

	@Command(tag = "store") public void store() throws BadLocationException, InterruptedException {
		storage.store(this);
	}
}
