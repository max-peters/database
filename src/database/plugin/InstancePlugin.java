package database.plugin;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.main.date.Date;

public abstract class InstancePlugin extends Plugin {
	protected Store						store;
	protected Terminal					terminal;
	protected GraphicalUserInterface	graphicalUserInterface;
	protected Administration			administration;
	protected InstanceList				instanceList;
	protected boolean					display;

	public InstancePlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		super(identity);
		this.store = store;
		this.terminal = terminal;
		this.graphicalUserInterface = graphicalUserInterface;
		this.administration = administration;
		this.instanceList = instanceList;
	}

	@Command(tag = "display") public void display() throws InterruptedException {
		String[][] displayInformation = { { identity, "(true|false)" } };
		try {
			display = Boolean.valueOf(request(displayInformation)[0][1]);
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

	protected String[][] request(String[][] information) throws CancellationException, InterruptedException {
		String[][] parameter = new String[information.length][2];
		for (int i = 0; i < information.length; i++) {
			String parameterInformation = administration.request(information[i][0], information[i][1]);
			parameter[i][0] = information[i][0];
			if (information[i][0].equals("date")) {
				parameter[i][1] = new Date(parameterInformation).toString();
			}
			else {
				parameter[i][1] = parameterInformation;
			}
		}
		return parameter;
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

	public void create(String[][] parameter) {
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

	@Getter public ArrayList<Instance> getList() {
		return instanceList.getList();
	}

	@Getter public String getDisplayAsString() {
		return "boolean : " + identity + " / " + display;
	}
}
