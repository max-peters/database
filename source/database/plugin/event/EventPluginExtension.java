package database.plugin.event;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.plugin.InstancePlugin;
import database.plugin.Storage;

public abstract class EventPluginExtension<T extends Event> extends InstancePlugin<T> {
	public EventPluginExtension(String identity, Storage storage, Class<T> instanceClass) {
		super(identity, storage, null, instanceClass);
	}

	@Override public void add(T event) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(event.date)) {
			i--;
		}
		list.add(i, event);
	}

	public abstract void createRequest() throws InterruptedException, BadLocationException;

	public List<Event> getEvents(LocalDate date) {
		List<Event> eventList = new LinkedList<Event>();
		for (Event event : getIterable()) {
			if (event.updateYear().isEqual(date)) {
				eventList.add(event);
			}
		}
		return eventList;
	}

	@Override public void display() throws InterruptedException, BadLocationException {
		super.display();
	}

	@Override public void initialOutput() throws BadLocationException {
		// no initial output here
	}

	@Override public void show() {
		// nothing to show here
	}

	@Override public void store() throws BadLocationException, InterruptedException {
		super.store();
	}
}
