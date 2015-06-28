package database.main;

import java.util.concurrent.CancellationException;

import javax.naming.directory.InvalidAttributesException;

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

	private String[] request(String[][] information) throws InterruptedException, CancellationException, InvalidAttributesException, IllegalAccessException {
		if (information == null) {
			throw new InvalidAttributesException();
		}
		if (information[0][0] == null) {
			throw new IllegalAccessException();
		}
		String[] parameter = new String[information.length];
		for (int i = 0; i < information.length; i++) {
			parameter[i] = administration.request(information[i][0], information[i][1]);
		}
		return parameter;
	}

	public void create() throws InterruptedException, InvalidAttributesException {
		try {
			create(request(getCreateInformation()));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
		catch (IllegalAccessException e) {
		}
	}

	public void show() throws InterruptedException, InvalidAttributesException {
		try {
			terminal.solutionOut(show(request(getShowInformation())));
			graphicalUserInterface.waitForInput();
		}
		catch (CancellationException e) {
			return;
		}
		catch (IllegalAccessException e) {
			terminal.solutionOut(show(null));
		}
	}

	public void change() throws InterruptedException, InvalidAttributesException {
		try {
			change(request(getChangeInformation()));
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
		catch (IllegalAccessException e) {
		}
	}

	public void display() throws InterruptedException, InvalidAttributesException {
		String[][] displayInformation = { { identity, "(true|false)" } };
		try {
			display = Boolean.valueOf(request(displayInformation)[0]);
			administration.update();
		}
		catch (CancellationException e) {
			return;
		}
		catch (IllegalAccessException e) {
		}
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean getDisplay() {
		return display;
	}

	public void store() {
		for (Object object : instanceList.getList()) {
			Instance instance = (Instance) object;
			store.addToStorage(instance.toString());
		}
		instanceList.getList().clear();
		administration.update();
	}

	public Instance check() throws InterruptedException {
		int position = graphicalUserInterface.check(instanceList.getEntriesAsStrings());
		if (position != -1) {
			return (Instance) instanceList.getList().get(position);
		}
		return null;
	}

	public void initialOutput() {
		String initialOutput = "";
		if (display) {
			initialOutput = instanceList.initialOutput();
		}
		terminal.out(initialOutput);
	}

	public void create(String[] parameter) {
		instanceList.add(parameter);
	}

	public void remove(Instance toRemove) {
		instanceList.remove(toRemove);
	}

	private String show(String[] parameter) {
		return instanceList.output(parameter);
	}

	abstract protected void change(String[] parameter);

	abstract protected String[][] getCreateInformation();

	abstract protected String[][] getShowInformation();

	abstract protected String[][] getChangeInformation();
}
