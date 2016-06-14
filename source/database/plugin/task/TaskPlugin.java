package database.plugin.task;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;
import database.plugin.backup.BackupService;

public class TaskPlugin extends InstancePlugin<Task> {
	public TaskPlugin(Storage storage) {
		super("task", storage, new TaskOutputFormatter(), Task.class);
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException {
		Task task = getTaskByCheckRequest();
		if (task != null) {
			BackupService.backupChangeBefor(task, this);
			task.name = Terminal.request("new name", ".+", task.name);
			BackupService.backupChangeAfter(task, this);
			Terminal.update();
		}
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException {
		Task task = getTaskByCheckRequest();
		if (task != null) {
			remove(task);
			BackupService.backupRemoval(task, this);
			Terminal.update();
		}
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException {
		String name = Terminal.request("name", ".+");
		String category = Terminal.request("category", ".+");
		String temp = Terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? null : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		while (date != null && date.isBefore(LocalDate.now())) {
			Terminal.errorMessage();
			temp = Terminal.request("date", "DATE");
			date = temp.isEmpty() ? null : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		}
		Task task = new Task(name, category, date);
		add(task);
		BackupService.backupCreation(task, this);
		Terminal.update();
	}

	@Override public void show() {
		// nothing to show here
	}

	private Task getTaskByCheckRequest() throws InterruptedException, BadLocationException {
		boolean display = getDisplay();
		String string;
		String[] splitResult;
		int position;
		Task temp = null;
		List<String> stringList = ((TaskOutputFormatter) formatter).formatOutput(list);
		setDisplay(false);
		Terminal.update();
		position = Terminal.checkRequest(stringList);
		setDisplay(display);
		Terminal.update();
		if (position != -1) {
			string = stringList.get(position);
		}
		else {
			return temp;
		}
		splitResult = string.split("-");
		while (splitResult[0].endsWith(" ")) {
			splitResult[0] = splitResult[0].substring(0, splitResult[0].length() - 1);
		}
		splitResult[1] = splitResult[1].substring(1);
		for (Task task : list) {
			if (task.category.equals(splitResult[0]) && (task.name.equals(splitResult[1]))) {
				temp = task;
			}
		}
		return temp;
	}
}