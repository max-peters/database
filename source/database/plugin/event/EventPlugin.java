package database.plugin.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.RequestInformation;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.holiday.HolidayPlugin;

public class EventPlugin extends InstancePlugin {
	private ArrayList<InstancePlugin> extentionList;

	public EventPlugin(PluginContainer pluginContainer, GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super(pluginContainer, graphicalUserInterface, administration, "event", null);
		extentionList = new ArrayList<InstancePlugin>();
		initialise();
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, IOException {
		EventPluginExtention extention = chooseType();
		if (extention != null) {
			extention.createRequest();
			update();
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
			EventList currentEventList = (EventList) extention.getInstanceList();
			for (Event event : currentEventList.getNearEvents()) {
				eventList.add(event);
			}
		}
		Collections.sort(eventList, new Comparator<Instance>() {
			@Override public int compare(Instance arg0, Instance arg1) {
				return ((Event) arg0).updateYear().compareTo(((Event) arg1).updateYear());
			}
		});
		for (Event event : eventList) {
			initialOutput = initialOutput + event.output() + "\r\n";
		}
		if (!initialOutput.isEmpty()) {
			initialOutput = "event" + ":\r\n" + initialOutput;
		}
		return initialOutput;
	}

	@Override public ArrayList<Instance> getList() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for (InstancePlugin extention : extentionList) {
			instances.addAll(extention.getList());
		}
		return instances;
	}

	private void initialise() throws IOException {
		extentionList.add(new AllDayEventPlugin(pluginContainer, graphicalUserInterface, administration));
		extentionList.add(new BirthdayPlugin(pluginContainer, graphicalUserInterface, administration));
		extentionList.add(new HolidayPlugin(pluginContainer, graphicalUserInterface, administration));
	}

	public List<RequestInformation> getInformationList() {
		List<RequestInformation> list = new ArrayList<RequestInformation>();
		for (InstancePlugin extention : extentionList) {
			for (Instance instance : extention.getList()) {
				Map<String, String> map = instance.getParameter();
				list.add(new RequestInformation(extention.getIdentity(), map));
			}
		}
		list.add(new RequestInformation("display", "boolean", String.valueOf(getDisplay())));
		return list;
	}

	public void readInformation(RequestInformation pair) throws IOException {
		if (pair.getName().equals("display")) {
			setDisplay(Boolean.valueOf(pair.getMap().get("boolean")));
		}
		else {
			for (InstancePlugin extention : extentionList) {
				if (pair.getName().equals(extention.getIdentity())) {
					extention.create(pair.getMap());
				}
			}
		}
	}

	private EventPluginExtention chooseType() throws InterruptedException {
		ArrayList<String> strings = new ArrayList<String>();
		EventPluginExtention toReturn = null;
		String pluginIdentity;
		int position;
		for (InstancePlugin extention : extentionList) {
			strings.add(extention.getIdentity());
		}
		strings.remove(2);
		position = graphicalUserInterface.check(strings);
		if (position != -1) {
			pluginIdentity = strings.get(position);
			for (InstancePlugin extention : extentionList) {
				if (pluginIdentity.equals(extention.getIdentity())) {
					toReturn = (EventPluginExtention) extention;
				}
			}
		}
		return toReturn;
	}
}
