package database.plugin;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;

public abstract class InstancePlugin extends Plugin {
	protected PluginContainer			pluginContainer;
	protected Terminal					terminal;
	protected GraphicalUserInterface	graphicalUserInterface;
	protected InstanceList				instanceList;
	protected boolean					display;
	private boolean						changes;

	public InstancePlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		super(identity, administration);
		this.pluginContainer = pluginContainer;
		this.terminal = terminal;
		this.graphicalUserInterface = graphicalUserInterface;
		this.instanceList = instanceList;
		this.changes = false;
	}

	@Command(tag = "display") public void display() throws InterruptedException {
		String[][] displayInformation = { { identity, "(true|false)" } };
		try {
			display = Boolean.valueOf(request(displayInformation)[0][1]);
			update();
		}
		catch (CancellationException e) {
			return;
		}
	}

	public void update() {
		graphicalUserInterface.clear();
		administration.initialOutput();
		setChanges(true);
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
		update();
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

	@Setter public void setChanges(boolean changes) {
		this.changes = changes;
	}

	@Getter public boolean getDisplay() {
		return display;
	}

	@Getter public boolean getChanges() {
		return changes;
	}

	@Getter public ArrayList<Instance> getList() {
		return instanceList.getList();
	}
}
