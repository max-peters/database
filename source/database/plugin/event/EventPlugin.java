package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Extendable;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.birthday.BirthdayPlugin;

public class EventPlugin extends InstancePlugin implements Extendable {
	private ArrayList<InstancePlugin>	extentionList;

	public EventPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "event", null);
		extentionList = new ArrayList<InstancePlugin>();
		initialise();
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			EventExtention extention = chooseType();
			if (extention != null) {
				extention.create();
				update();
			}
		}
		catch (CancellationException e) {
			return;
		}
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

	@Override public void create(String[][] parameter) {
		for (InstancePlugin extention : extentionList) {
			for (int i = 0; i < parameter.length; i++) {
				if (parameter[i][0].equals("type")) {
					if (parameter[i][1].equals(extention.getIdentity())) {
						int k = (i + 1) % 3;
						extention.create(new String[][] { parameter[k], parameter[(k + 1) % 3] });
					}
				}
			}
		}
	}

	@Override public void remove(Instance toRemove) {
		for (InstancePlugin extention : extentionList) {
			if (extention.getList().contains(toRemove)) {
				extention.getList().remove(toRemove);
			}
		}
	}

	@Override public void initialOutput() {
		String initialOutput = "";
		List<Event> eventList = new ArrayList<Event>();
		if (display) {
			for (InstancePlugin extention : extentionList) {
				if (extention.getDisplay()) {
					EventList currentEventList = (EventList) ((EventExtention) extention).getInstanceList();
					for (Event event : currentEventList.getNearEvents()) {
						eventList.add(event);
					}
				}
			}
			Collections.sort(eventList);
			for (Event event : eventList) {
				initialOutput = initialOutput + event.output() + "\r\n";
			}
			if (!initialOutput.isEmpty()) {
				initialOutput = "event" + ":\r\n" + initialOutput;
			}
			terminal.out(initialOutput);
		}
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
			strings.add(event.identity);
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			return events.get(position);
		}
		return null;
	}

	@Override public ArrayList<Instance> getList() {
		ArrayList<Event> events = new ArrayList<Event>();
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for (InstancePlugin extention : extentionList) {
			for (Instance instance : extention.getList()) {
				Event event = (Event) instance;
				events.add(event);
			}
		}
		Collections.sort(events);
		for (Event event : events) {
			instances.add(event);
		}
		return instances;
	}

	@Override public void initialise() {
		extentionList.add(new AllDayEventPlugin(pluginContainer, terminal, graphicalUserInterface, administration));
		extentionList.add(new BirthdayPlugin(pluginContainer, terminal, graphicalUserInterface, administration));
	}

	@Override public ArrayList<InstancePlugin> getExtentions() {
		return extentionList;
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