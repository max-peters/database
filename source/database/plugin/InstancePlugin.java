package database.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;

public abstract class InstancePlugin extends Plugin {
	protected PluginContainer	pluginContainer;
	protected InstanceList		instanceList;

	public InstancePlugin(	PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity,
							InstanceList instanceList) {
		super(identity, administration, graphicalUserInterface);
		this.pluginContainer = pluginContainer;
		this.graphicalUserInterface = graphicalUserInterface;
		this.instanceList = instanceList;
	}

	@Override public String initialOutput() {
		String initialOutput = instanceList.initialOutput();
		if (initialOutput != null && !initialOutput.isEmpty()) {
			initialOutput = identity + ":\r\n" + initialOutput;
		}
		return initialOutput;
	}

	@Override public List<RequestInformation> getInformationList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		for (int i = 0; i < getList().size(); i++) {
			list.add(new RequestInformation("entry", getList().get(i).getParameter()));
		}
		list.add(new RequestInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
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

	public Instance check() throws InterruptedException {
		int position;
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : getList()) {
			strings.add(instance.getIdentity());
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			return getList().get(position);
		}
		return null;
	}

	public void create(Map<String, String> map) throws IOException {
		instanceList.add(map);
	}

	public void remove(Instance toRemove) {
		instanceList.remove(toRemove);
		update();
	}

	public ArrayList<Instance> getList() {
		return instanceList.getList();
	}

	public InstanceList getInstanceList() {
		return instanceList;
	}
}
