package database.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;

public abstract class Plugin {
	protected Store						store;
	protected Terminal					terminal;
	protected GraphicalUserInterface	graphicalUserInterface;
	protected Administration			administration;
	protected String					identity;
	protected InstanceList				instanceList;
	protected boolean					display;

	public Plugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		this.store = store;
		this.terminal = terminal;
		this.graphicalUserInterface = graphicalUserInterface;
		this.administration = administration;
		this.identity = identity;
		this.instanceList = instanceList;
	}

	@Command(tag = "display") public void display() throws InterruptedException {
		String[][] displayInformation = { { identity, "(true|false)" } };
		try {
			display = Boolean.valueOf(request(displayInformation)[0]);
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Command(tag = "store") public void store() {
		for (Object object : instanceList.getList()) {
			Instance instance = (Instance) object;
			store.addToStorage(instance.toString());
		}
		instanceList.getList().clear();
		administration.update();
	}

	protected String[] request(String[][] information) throws CancellationException, InterruptedException {
		String[] parameter = new String[information.length];
		for (int i = 0; i < information.length; i++) {
			parameter[i] = administration.request(information[i][0], information[i][1]);
		}
		return parameter;
	}

	public String getCommandTags() {
		String regex = "(";
		ArrayList<String> strings = new ArrayList<String>();
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				strings.add(method.getAnnotation(Command.class).tag());
			}
		}
		for (String string : strings) {
			regex = regex + string;
			if (!(strings.indexOf(string) == strings.size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}

	public Instance check() throws InterruptedException {
		int position;
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : instanceList.getList()) {
			strings.add(instance.identity);
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			return instanceList.getList().get(position);
		}
		return null;
	}

	public void create(String[] parameter) {
		instanceList.add(parameter);
	}

	public void remove(Instance toRemove) {
		instanceList.remove(toRemove);
	}

	public void initialOutput() {
		String initialOutput = "";
		if (display) {
			initialOutput = instanceList.initialOutput();
		}
		if (!initialOutput.isEmpty()) {
			initialOutput = identity + ":\r\n" + initialOutput;
		}
		terminal.out(initialOutput);
	}

	@Setter public void setDisplay(boolean display) {
		this.display = display;
	}

	@Getter public boolean getDisplay() {
		return display;
	}

	@Getter public ArrayList<String> getStringList() {
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : instanceList.getList()) {
			strings.add(instance.toString());
		}
		return strings;
	}

	@Getter public String getDisplayAsString() {
		return "boolean : " + identity + " / " + display;
	}

	@Getter public String getIdentity() {
		return identity;
	}

	public abstract void conduct(String command) throws InterruptedException;
}
