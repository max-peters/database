package database.plugin.task;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class TaskPlugin extends InstancePlugin<Task> {
	public TaskPlugin(Storage storage, Backup backup) {
		super("task", storage, new TaskOutputFormatter(), backup);
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
		Task task = getTaskByCheckRequest();
		backup.backupChangeBefor(task, this);
		task.name = Terminal.request("new name", ".+", task.name);
		backup.backupChangeAfter(task, this);
		update();
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException {
		remove(getTaskByCheckRequest());
		update();
	}

	@Override public Task create(Map<String, String> parameter) {
		return new Task(parameter.get("name"), parameter.get("category"));
	}

	@Override public Task create(NamedNodeMap nodeMap) {
		return new Task(nodeMap.getNamedItem("name").getNodeValue(), nodeMap.getNamedItem("category").getNodeValue());
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", ".+");
		map.put("category", ".+");
		request(map);
		createAndAdd(map);
		update();
	}

	@Override public void show() {
		// nothing to show here
	}

	public void add(Task toAdd) {
		int position = -1;
		for (Task task : list) {
			if (task.category.equals(toAdd.category)) {
				position = list.indexOf(task);
				break;
			}
		}
		list.add(position + 1, toAdd);
	}

	private Task getTaskByCheckRequest() throws InterruptedException, BadLocationException {
		boolean display = getDisplay();
		int position;
		setDisplay(false);
		Terminal.update();
		position = Terminal.checkRequest(((TaskOutputFormatter) formatter).formatOutput(list));
		setDisplay(display);
		Terminal.update();
		if (position != -1) {
			return list.get(position);
		}
		return null;
	}
}