package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.RequestInformation;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.birthday.BirthdayPlugin;

public class EventPlugin extends InstancePlugin {
	private ArrayList<InstancePlugin>	extentionList;

	public EventPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "event", null);
		extentionList = new ArrayList<InstancePlugin>();
		initialise();
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		chooseType().create();
		update();
	}

	@Override @Command(tag = "display") public void display() throws InterruptedException {
		chooseType().display();
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "display":
				display();
				break;
		}
	}

	@Override public void create(Map<String, String> map) {
		for (InstancePlugin extention : extentionList) {
			if (map.get("type").equals(extention.getIdentity())) {
				extention.create(map);
			}
		}
	}

	@Override public void remove(Instance toRemove) {
		for (InstancePlugin extention : extentionList) {
			extention.getList().remove(toRemove);
		}
	}

	@Override public String initialOutput() {
		String initialOutput = "";
		List<Event> eventList = new ArrayList<Event>();
		for (InstancePlugin extention : extentionList) {
			EventList currentEventList = (EventList) ((EventExtention) extention).getInstanceList();
			for (Event event : currentEventList.getNearEvents()) {
				eventList.add(event);
			}
		}
		Collections.sort(eventList);
		for (Event event : eventList) {
			initialOutput = initialOutput + event.output() + "\r\n";
		}
		if (!initialOutput.isEmpty()) {
			initialOutput = "event" + ":\r\n" + initialOutput;
		}
		return initialOutput;
	}

	@Override public Instance check() throws InterruptedException {
		int position;
		ArrayList<String> strings = new ArrayList<String>();
		ArrayList<Event> events = new ArrayList<Event>();
		for (InstancePlugin extention : extentionList) {
			for (Instance instance : extention.getList()) {
				Event event = (Event) instance;
				events.add(event);
			}
		}
		Collections.sort(events);
		for (Event event : events) {
			strings.add(event.getIdentity());
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			return events.get(position);
		}
		return null;
	}

	@Override public ArrayList<Instance> getList() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for (InstancePlugin extention : extentionList) {
			instances.addAll(extention.getList());
		}
		return instances;
	}

	public void initialise() {
		extentionList.add(new AllDayEventPlugin(pluginContainer, terminal, graphicalUserInterface, administration));
		extentionList.add(new BirthdayPlugin(pluginContainer, terminal, graphicalUserInterface, administration));
	}

	public List<RequestInformation> getPairList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		Collections.sort(getList());
		for (int i = 0; i < getList().size(); i++) {
			Map<String, String> map = getList().get(i).getParameter();
			list.add(new RequestInformation(map.remove("type"), map));
		}
		list.add(new RequestInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	public void create(RequestInformation pair) {
		if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else {
			for (InstancePlugin extention : extentionList) {
				if (pair.getName().equals(extention.getIdentity())) {
					pair.getMap().put("type", extention.getIdentity());
					extention.create(pair.getMap());
				}
			}
		}
	}

	private EventExtention chooseType() throws InterruptedException {
		ArrayList<String> strings = new ArrayList<String>();
		EventExtention toReturn = null;
		String pluginIdentity;
		int position;
		for (InstancePlugin extention : extentionList) {
			strings.add(extention.getIdentity());
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (InstancePlugin extention : extentionList) {
				if (pluginIdentity.equals(extention.getIdentity())) {
					toReturn = (EventExtention) extention;
				}
			}
		}
		return toReturn;
	}
}
