package database.plugin.task;

import java.io.IOException;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;

public class TaskPlugin extends InstancePlugin<Task> {
	public TaskPlugin(Storage storage, Backup backup) {
		super("task", storage, new TaskOutputFormatter(), backup);
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
		Task task = getTaskByCheckRequest();
		backup.backup();
		task.name = Terminal.request("new name", ".+", task.name);
		Terminal.update();
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException {
		backup.backup();
		remove(getTaskByCheckRequest());
		Terminal.update();
	}

	@Override public Task create(NamedNodeMap nodeMap) {
		return new Task(nodeMap.getNamedItem("name").getNodeValue(), nodeMap.getNamedItem("category").getNodeValue());
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		backup.backup();
		add(new Task(Terminal.request("name", ".+"), Terminal.request("category", ".+")));
		Terminal.update();
	}

	@Override public void show() {
		// nothing to show here
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