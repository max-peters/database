package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Instance;
import database.main.InstanceList;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.allDayEvent.birthday.BirthdayPlugin;

public class EventPlugin extends Plugin {
	private ArrayList<Plugin>	pluginList	= new ArrayList<Plugin>();

	public EventPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "event");
		this.instanceList = new EventList();
		initialise();
	}

	public void create() throws InterruptedException {
		try {
			Plugin plugin = chooseType();
			if (plugin != null) {
				plugin.create(request(plugin.getCreateInformation()));
				administration.update();
			}
		}
		catch (CancellationException e) {
			return;
		}
	}

	public void create(String[] parameter) {
		for (Plugin plugin : pluginList) {
			if (parameter[0].equals(plugin.getIdentity())) {
				plugin.create(new String[] { parameter[1], parameter[2] });
			}
		}
	}

	public InstanceList getInstanceList() {
		EventList events = new EventList();
		for (Plugin plugin : pluginList) {
			for (Instance instance : plugin.getInstanceList().getList()) {
				events.getList().add(instance);
			}
		}
		return events;
	}

	public void initialOutput() {
		String initialOutput = "";
		List<Event> eventList = new ArrayList<Event>();
		if (display) {
			for (Plugin plugin : pluginList) {
				if (plugin.getDisplay()) {
					EventList currentEventList = (EventList) plugin.getInstanceList();
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

	public void display() throws InterruptedException {
		chooseType().display();
	}

	public void store() {
		for (Plugin plugin : pluginList) {
			for (Object object : plugin.getInstanceList().getList()) {
				Instance instance = (Instance) object;
				store.addToStorage(instance.toString());
			}
			instanceList.getList().clear();
		}
		administration.update();
	}

	private Plugin chooseType() throws InterruptedException {
		ArrayList<String> strings = new ArrayList<String>();
		Plugin toReturn = null;
		String pluginIdentity;
		int position;
		for (Plugin plugin : pluginList) {
			strings.add(plugin.getIdentity());
		}
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (Plugin plugin : pluginList) {
				if (pluginIdentity.equals(plugin.getIdentity())) {
					toReturn = plugin;
				}
			}
		}
		return toReturn;
	}

	private void initialise() {
		pluginList.add(new AllDayEventPlugin(store, terminal, graphicalUserInterface, administration));
		pluginList.add(new BirthdayPlugin(store, terminal, graphicalUserInterface, administration));
	}

	public String[][] getCreateInformation() throws NotImplementedException {
		String[][] createInformation = { { "name", "[A-ZÖÄÜ][a-zöäüß]*($|([- ][A-ZÖÄÜ][a-zöäüß]*)+)" }, { "date", null } };
		return createInformation;
	}

	public String[][] getShowInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}

	public String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
