package database.plugin.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class TaskPlugin extends InstancePlugin<Task> {
	public TaskPlugin(Storage storage) {
		super("task", storage, new TaskOutputFormatter());
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
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

	@Override public Task create(Map<String, String> parameter) {
		return new Task(parameter.get("name"));
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", ".+");
		request(map);
		createAndAdd(map);
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
		for (Task task : getIterable()) {
			strings.add(task.name);
		}
		position = Terminal.checkRequest(strings);
		setDisplay(display);
		Terminal.update();
		if (position != -1) {
			return list.get(position);
		}
		return null;
	}
}