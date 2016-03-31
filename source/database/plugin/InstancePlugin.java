package database.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.storage.Storage;

public abstract class InstancePlugin<T extends Instance> extends Plugin {
	protected InstanceList<T>	instanceList;
	private Storage				storage;

	public InstancePlugin(String identity, InstanceList<T> instanceList, Storage storage) {
		super(identity);
		this.instanceList = instanceList;
		this.storage = storage;
	}

	public void clearList() {
		instanceList.clear();
	}

	public abstract T create(Map<String, String> map)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException;

	public void createAndAdd(Map<String, String> map)	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException, IOException {
		instanceList.add(create(map));
	}

	public InstanceList<T> getInstanceList() {
		return instanceList;
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = instanceList.initialOutput();
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(getIdentity() + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public List<PrintInformation> print() {
		List<PrintInformation> list = new ArrayList<PrintInformation>();
		for (Instance instance : instanceList) {
			list.add(new PrintInformation("entry", instance.getParameter()));
		}
		list.add(new PrintInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void read(PrintInformation pair)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
														NoSuchMethodException, SecurityException {
		if (pair.getName().equals("entry")) {
			createAndAdd(pair.getMap());
		}
		else if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
	}

	public void remove(Instance toRemove) throws BadLocationException {
		instanceList.remove(toRemove);
		update();
	}

	@Command(tag = "show") public void show() throws InterruptedException, BadLocationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String command = Terminal.request("show", getCommandTags(instanceList.getClass()));
		for (Method method : instanceList.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getAnnotation(Command.class).tag().equals(command)) {
				Object output = method.invoke(instanceList, (Object[]) method.getParameterTypes());
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
