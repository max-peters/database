package database.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;

public abstract class InstancePlugin extends Plugin {
	protected PluginContainer	pluginContainer;
	protected Terminal			terminal;
	protected InstanceList		instanceList;

	public InstancePlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration, String identity, InstanceList instanceList) {
		super(identity, administration, graphicalUserInterface);
		this.pluginContainer = pluginContainer;
		this.terminal = terminal;
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

	@Override public List<Pair> getPairList() {
		List<Pair> list = new ArrayList<Pair>();
		Collections.sort(instanceList.getList());
		for (int i = 0; i < instanceList.getList().size(); i++) {
			list.add(new Pair("entry", instanceList.getList().get(i).getParameter()));
		}
		list.add(new Pair("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	@Override public void create(Pair pair) {
		if (pair.getName().equals("entry")) {
			create(pair.getMap());
		}
		else if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else {
		}
	}

	public Instance check() throws InterruptedException {
		int position;
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : instanceList.getList()) {
			strings.add(instance.getIdentity());
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			return instanceList.getList().get(position);
		}
		return null;
	}

	public void create(Map<String, String> map) {
		instanceList.add(map);
	}

	public void remove(Instance toRemove) {
		instanceList.remove(toRemove);
		update();
	}

	public ArrayList<Instance> getList() {
		Collections.sort(instanceList.getList());
		return instanceList.getList();
	}
}
