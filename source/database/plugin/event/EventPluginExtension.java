package database.plugin.event;

import javax.swing.text.BadLocationException;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public abstract class EventPluginExtension<T extends Event> extends InstancePlugin<T> {
	public EventPluginExtension(String identity, Storage storage) {
		super(identity, storage, null);
	}

	@Override public void add(T event) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.compareTo(event.date) > 0) {
			i--;
		}
		list.add(i, event);
	}

	public abstract void createRequest() throws InterruptedException, BadLocationException;

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
