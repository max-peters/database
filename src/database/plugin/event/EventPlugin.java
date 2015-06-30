package database.plugin.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.allDayEvent.birthday.BirthdayPlugin;

public class EventPlugin extends Plugin {
	private ArrayList<Plugin>	pluginList	= new ArrayList<Plugin>();

	public EventPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "event");
		pluginList = new ArrayList<Plugin>();
	}

	public void initialOutput() {
		String initialOutput = "";
		List<Event> eventList = new ArrayList<Event>();
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

	public void fetchPlugins() {
		pluginList.add(new AllDayEventPlugin(store, terminal, graphicalUserInterface, administration));
		pluginList.add(new BirthdayPlugin(store, terminal, graphicalUserInterface, administration));
	}

	public ArrayList<Plugin> getPluginList() {
		return pluginList;
	}

	protected String[][] getCreateInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}

	protected String[][] getShowInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}

	protected String[][] getChangeInformation() throws NotImplementedException {
		throw new NotImplementedException();
	}
}
