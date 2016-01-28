package database.plugin.task;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class TaskPlugin extends InstancePlugin {
	public TaskPlugin(Storage storage) {
		super("task", new TaskList(), storage);
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException {
		boolean display = getDisplay();
		int position;
		setDisplay(false);
		Terminal.update();
		ArrayList<String> strings = new ArrayList<String>();
		for (Instance instance : getInstanceList().getIterable()) {
			Task task = (Task) instance;
			strings.add(task.name);
		}
		position = Terminal.checkRequest(strings);
		if (position != -1) {
			remove(getInstanceList().get(position));
		}
		setDisplay(display);
		Terminal.update();
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", ".+");
		request(map);
		create(map);
		update();
	}

	@Override public void show() {
		// nothing to show here
	}
}