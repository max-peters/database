package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.allDayEvent.birthday.BirthdayPlugin;

public class EventPlugin extends InstancePlugin {
	private ArrayList<EventExtention>	extentionList	= new ArrayList<EventExtention>();

	public EventPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "event", null);
		initialise();
	}

	@Command(tag = "new") public void create() throws InterruptedException {
		try {
			EventExtention extention = chooseType();
			if (extention != null) {
				extention.create();
				administration.update();
			}
		}
		catch (CancellationException e) {
			return;
		}
	}

	@Override @Command(tag = "display") public void display() throws InterruptedException {
		chooseType().display();
	}

	@Override @Command(tag = "store") public void store() {
		for (EventExtention extention : extentionList) {
			for (Object object : extention.getInstanceList().getList()) {
				Instance instance = (Instance) object;
				store.addToStorage(instance.toString());
			}
			extention.getInstanceList().getList().clear();
		}
		administration.update();
	}

	@Override public void conduct(String command) throws InterruptedException {
		switch (command) {
			case "new":
				create();
				break;
			case "store":
				store();
				break;
			case "display":
				display();
				break;
		}
	}

	@Override public void create(String[][] parameter) {
		for (EventExtention extention : extentionList) {
			if (parameter[0].equals(extention.getIdentity())) {
				extention.create(new String[][] { parameter[1], parameter[2] });
			}
		}
	}

	@Override public void remove(Instance toRemove) {
		for (EventExtention extention : extentionList) {
			if (extention.getInstanceList().getList().contains(toRemove)) {
				extention.getInstanceList().getList().remove(toRemove);
			}
		}
	}

	@Override public void initialOutput() {
		String initialOutput = "";
		List<Event> eventList = new ArrayList<Event>();
		if (display) {
			for (EventExtention extention : extentionList) {
				if (extention.getDisplay()) {
					EventList currentEventList = (EventList) extention.getInstanceList();
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
		for (EventExtention extention : extentionList) {
			for (Instance instance : extention.getInstanceList().getList()) {
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
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for (EventExtention extention : extentionList) {
			for (Instance instance : extention.getInstanceList().getList()) {
				instances.add(instance);
			}
		}
		return instances;
	}

	private EventExtention chooseType() throws InterruptedException {
		ArrayList<String> strings = new ArrayList<String>();
		EventExtention toReturn = null;
		String pluginIdentity;
		int position;
		for (EventExtention extention : extentionList) {
			strings.add(extention.getIdentity());
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (EventExtention extention : extentionList) {
				if (pluginIdentity.equals(extention.getIdentity())) {
					toReturn = extention;
				}
			}
		}
		return toReturn;
	}

	private void initialise() {
		extentionList.add(new AllDayEventPlugin(store, terminal, graphicalUserInterface, administration));
		extentionList.add(new BirthdayPlugin(store, terminal, graphicalUserInterface, administration));
	}
}
