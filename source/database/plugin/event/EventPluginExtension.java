package database.plugin.event;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.Terminal;
import database.plugin.FormatterProvider;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;

public abstract class EventPluginExtension<T extends Event> extends InstancePlugin<T> {
	public EventPluginExtension(String identity, Class<T> instanceClass) {
		super(identity, instanceClass);
	}

	@Override public void add(T event) {
		int i = list.size();
		while (i > 0 && list.get(i - 1).date.isAfter(event.date)) {
			i--;
		}
		list.add(i, event);
	}

	public abstract void createRequest(Terminal terminal, BackupService backupService) throws InterruptedException, BadLocationException;

	@Override public void display(Terminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws InterruptedException, BadLocationException {
		super.display(terminal, pluginContainer, formatterProvider);
	}

	public List<Event> getEvents(LocalDate date) {
		List<Event> eventList = new LinkedList<Event>();
		for (Event event : getIterable()) {
			if (event.updateYear().isEqual(date)) {
				eventList.add(event);
			}
		}
		return eventList;
	}

	@Override public void initialOutput(Terminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException {
		// no initial output here
	}

	@Override public void show(Terminal terminal, FormatterProvider formatterProvider) {
		// nothing to show here
	}

	@Override public void store(PluginContainer pluginContainer, Terminal terminal, FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		super.store(pluginContainer, terminal, formatterProvider);
	}
}
