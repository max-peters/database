package database.plugin;

import java.awt.print.PrinterAbortException;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;

public abstract class Plugin {
	protected Terminal					terminal;
	protected GraphicalUserInterface	graphicalUserInterface;
	protected Administration			administration;
	protected boolean					display;
	protected InstanceList				instanceList;
	protected String					identity;
	protected Store						store;

	public Plugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity) {
		this.store = store;
		this.terminal = terminal;
		this.graphicalUserInterface = graphicalUserInterface;
		this.administration = administration;
		this.identity = identity;
	}

	protected String[] request(String[][] information) throws CancellationException, InterruptedException {
		String[] parameter = new String[information.length];
		for (int i = 0; i < information.length; i++) {
			parameter[i] = administration.request(information[i][0], information[i][1]);
		}
		return parameter;
	}

	public void commandRequest() {
	}

	public void create() throws InterruptedException {
		try {
			create(request(getCreateInformation()));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	public void show() throws InterruptedException, NotImplementedException {
		try {
			terminal.solutionOut(show(request(getShowInformation())));
			graphicalUserInterface.waitForInput();
		}
		catch (CancellationException e) {
			return;
		}
		catch (PrinterAbortException e) {
			terminal.solutionOut(show(null));
		}
	}

	public void change() throws InterruptedException, NotImplementedException {
		try {
			change(request(getChangeInformation()));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	public void display() throws InterruptedException {
		String[][] displayInformation = { { identity, "(true|false)" } };
		try {
			display = Boolean.valueOf(request(displayInformation)[0]);
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	public Instance check() throws InterruptedException {
		int position = graphicalUserInterface.check(instanceList.getEntriesAsStrings());
		if (position != -1) {
			return instanceList.getList().get(position);
		}
		return null;
	}

	public void store() {
		for (Object object : getInstanceList().getList()) {
			Instance instance = (Instance) object;
			store.addToStorage(instance.toString());
		}
		instanceList.getList().clear();
		administration.update();
	}

	public void create(String[] parameter) {
		instanceList.add(parameter);
	}

	public void remove(Instance toRemove) {
		instanceList.remove(toRemove);
	}

	public void change(String[] parameter) {
		instanceList.change(parameter);
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

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean getDisplay() {
		return display;
	}

	public InstanceList getInstanceList() {
		return instanceList;
	}

	public ArrayList<String> getStringList() {
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : getInstanceList().getList()) {
			strings.add(instance.toString());
		}
		return strings;
	}

	public String getDisplayAsString() {
		return "boolean : " + identity + " / " + display;
	}

	public String getIdentity() {
		return identity;
	}

	private String show(String[] parameter) {
		return instanceList.output(parameter);
	}

	public abstract String[][] getCreateInformation() throws NotImplementedException;

	public abstract String[][] getShowInformation() throws PrinterAbortException, NotImplementedException;

	public abstract String[][] getChangeInformation() throws NotImplementedException;
}
