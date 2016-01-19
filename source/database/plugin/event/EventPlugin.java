package database.plugin.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.RequestInformation;
import database.plugin.event.allDayEvent.AllDayEventPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.holiday.HolidayPlugin;

public class EventPlugin extends InstancePlugin {
	private ArrayList<InstancePlugin> extentionList;

	public EventPlugin(PluginContainer pluginContainer) {
		super(pluginContainer, "event", null);
		extentionList = new ArrayList<InstancePlugin>();
		initialise();
	}

	@Override public void clearList() {
		for (InstancePlugin extention : extentionList) {
			extention.clearList();
		}
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		EventPluginExtention extention = chooseType();
		if (extention != null) {
			extention.createRequest();
			update();
		}
	}

	@Override public List<RequestInformation> getInformationList() {
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

	@Override public ArrayList<Instance> getList() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		for (InstancePlugin extention : extentionList) {
			instances.addAll(extention.getList());
		}
		return instances;
	}

	@Override public void initialOutput() throws BadLocationException {
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
			initialOutput = initialOutput + event.output() + System.getProperty("line.separator");
		}
		if (!initialOutput.isEmpty()) {
			Terminal.printLine(identity + ":", StringType.MAIN, StringFormat.BOLD);
			Terminal.printLine(initialOutput, StringType.MAIN, StringFormat.STANDARD);
		}
	}

	@Override public void readInformation(RequestInformation pair) throws IOException {
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

	@Override public void remove(Instance toRemove) {
		for (InstancePlugin extention : extentionList) {
			extention.getList().remove(toRemove);
		}
	}

	@Override public void show() {
		// nothing to show here
	}

	private EventPluginExtention chooseType() throws InterruptedException, BadLocationException {
		ArrayList<String> strings = new ArrayList<String>();
		EventPluginExtention toReturn = null;
		String pluginIdentity;
		int position;
		for (InstancePlugin extention : extentionList) {
			strings.add(extention.getIdentity());
		}
		strings.remove(2);
		position = Terminal.checkRequest(strings);
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

	private void initialise() {
		extentionList.add(new AllDayEventPlugin(pluginContainer));
		extentionList.add(new BirthdayPlugin(pluginContainer));
		extentionList.add(new HolidayPlugin(pluginContainer));
	}
}
