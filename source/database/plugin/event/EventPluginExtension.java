package database.plugin.event;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;

public abstract class EventPluginExtension<T extends Event> extends InstancePlugin<T> {
	public EventPluginExtension(String identity, Class<T> type) {
		super(identity, null, type);
	}

	@Override public void add(T event) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(event.date)) {
			i--;
		}
		list.add(i, event);
	}

	public abstract void createRequest(ITerminal terminal, BackupService backupService) throws InterruptedException, BadLocationException, UserCancelException;

	@Override public void display(ITerminal terminal, PluginContainer pluginContainer) throws InterruptedException, BadLocationException, UserCancelException {
		super.display(terminal, pluginContainer);
	}

	public List<Event> getEvents(LocalDate date) {
		List<Event> eventList = new LinkedList<>();
		for (Event event : getIterable()) {
			if (event.updateYear().isEqual(date)) {
				eventList.add(event);
			}
		}
		return eventList;
	}

	@Override public void initialOutput(ITerminal terminal, PluginContainer pluginContainer) throws BadLocationException {
		// no initial output here
	}

	@Override public void show(ITerminal terminal) {
		// nothing to show here
	}

	@Override public void store(PluginContainer pluginContainer, ITerminal terminal) throws BadLocationException, InterruptedException, UserCancelException {
		super.store(pluginContainer, terminal);
	}
}
