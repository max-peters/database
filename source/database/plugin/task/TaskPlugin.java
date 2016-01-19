package database.plugin.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class TaskPlugin extends InstancePlugin {
	public TaskPlugin(PluginContainer pluginContainer, Storage storage) {
		super(pluginContainer, "task", new TaskList(), storage);
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException, IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", ".+");
		request(map);
		create(map);
		update();
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException {
		boolean display = getDisplay();
		setDisplay(false);
		Terminal.update();
		remove(check());
		setDisplay(display);
	}

	@Override public void show() {
		// nothing to show here
	}
}