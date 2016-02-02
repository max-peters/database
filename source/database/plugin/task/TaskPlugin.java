package database.plugin.task;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

	@Command(tag = "edit") public void changeRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Task task = getTaskByCheckRequest();
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("new name", ".+");
		request(map);
		task.name = map.get("new name");
		update();
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException {
		remove(getTaskByCheckRequest());
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", ".+");
		request(map);
		create(map);
		update();
	}

	@Override public void show() {
		// nothing to show here
	}

	private Task getTaskByCheckRequest() throws InterruptedException, BadLocationException {
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
		setDisplay(display);
		Terminal.update();
		if (position != -1) {
			return (Task) getInstanceList().get(position);
		}
		return null;
	}
}