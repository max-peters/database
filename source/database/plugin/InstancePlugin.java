package database.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.Terminal;
import database.main.GraphicalUserInterface.StringFormat;
import database.main.GraphicalUserInterface.StringType;

public abstract class InstancePlugin extends Plugin {
	protected InstanceList		instanceList;
	protected PluginContainer	pluginContainer;

	public InstancePlugin(PluginContainer pluginContainer, String identity, InstanceList instanceList) {
		super(identity);
		this.pluginContainer = pluginContainer;
		this.instanceList = instanceList;
	}

	public Instance check() throws InterruptedException, BadLocationException {
		int position;
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : getList()) {
			strings.add(instance.getIdentity());
		}
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			return getList().get(position);
		}
		return null;
	}

	public void create(Map<String, String> map) throws IOException {
		instanceList.add(map);
	}

	@Override public List<RequestInformation> getInformationList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		for (int i = 0; i < getList().size(); i++) {
			list.add(new RequestInformation("entry", getList().get(i).getParameter()));
		}
		list.add(new RequestInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	public InstanceList getInstanceList() {
		return instanceList;
	}

	public ArrayList<Instance> getList() {
		return instanceList.getList();
	}

	@Override public void initialOutput() throws BadLocationException {
		String initialOutput = instanceList.initialOutput();
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(identity + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void readInformation(RequestInformation pair) throws IOException {
		if (pair.getName().equals("entry")) {
			create(pair.getMap());
		}
		else if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else {
			throw new IOException();
		}
	}

	public void remove(Instance toRemove) throws BadLocationException {
		instanceList.remove(toRemove);
		update();
	}
}
