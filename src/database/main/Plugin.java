package database.main;

import java.awt.print.PrinterAbortException;
import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class Plugin {
	private Terminal				terminal;
	private GraphicalUserInterface	graphicalUserInterface;
	private Administration			administration;
	private boolean					display;
	protected InstanceList			instanceList;
	protected String				identity;
	protected Store					store;

	public Plugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity) {
		this.store = store;
		this.terminal = terminal;
		this.graphicalUserInterface = graphicalUserInterface;
		this.administration = administration;
		this.identity = identity;
	}

	private String[] request(String[][] information) throws CancellationException, InterruptedException {
		String[] parameter = new String[information.length];
		for (int i = 0; i < information.length; i++) {
			parameter[i] = administration.request(information[i][0], information[i][1]);
		}
		return parameter;
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
			return (Instance) instanceList.getList().get(position);
		}
		return null;
	}

	public void store() {
		for (Object object : instanceList.getList()) {
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
		terminal.out(initialOutput);
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean getDisplay() {
		return display;
	}

	private String show(String[] parameter) {
		return instanceList.output(parameter);
	}

	abstract protected String[][] getCreateInformation();

	abstract protected String[][] getShowInformation() throws PrinterAbortException, NotImplementedException;

	abstract protected String[][] getChangeInformation() throws NotImplementedException;
}
