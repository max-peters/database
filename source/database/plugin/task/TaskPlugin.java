package database.plugin.task;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;
import database.services.database.IDatabase;

public class TaskPlugin extends InstancePlugin<Task> {
	public TaskPlugin() {
		super("task", new TaskOutputFormatter(), Task.class);
	}

	@Command(tag = "edit") public void changeRequest(	ITerminal terminal, BackupService backupService,
														PluginContainer pluginContainer) throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		Task task = getTaskByCheckRequest(terminal, pluginContainer);
		if (task != null) {
			backupService.backupChangeBefor(task, this);
			task.name = terminal.request("new name", ".+", task.name);
			backupService.backupChangeAfter(task, this);
			terminal.update(pluginContainer);
		}
	}

	@Command(tag = "check") public void checkRequest(ITerminal terminal, BackupService backupService, PluginContainer pluginContainer)	throws InterruptedException,
																																		BadLocationException, IOException {
		Task task = getTaskByCheckRequest(terminal, pluginContainer);
		if (task != null) {
			remove(task);
			backupService.backupRemoval(task, this);
			terminal.update(pluginContainer);
		}
	}

	@Command(tag = "new") public void createRequest(ITerminal terminal, BackupService backupService,
													PluginContainer pluginContainer) throws BadLocationException, InterruptedException, UserCancelException, SQLException {
		String name = terminal.request("name", ".+");
		String category = terminal.request("category", ".+");
		String temp = terminal.request("date", "DATE");
		LocalDate date = temp.isEmpty() ? null : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		while (date != null && date.isBefore(LocalDate.now())) {
			terminal.errorMessage();
			temp = terminal.request("date", "DATE");
			date = temp.isEmpty() ? null : LocalDate.parse(temp, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		}
		Task task = new Task(name, category, date);
		add(task);
		backupService.backupCreation(task, this);
		terminal.update(pluginContainer);
	}

	@Override public void show(ITerminal terminal, IDatabase database) {
		// nothing to show here
	}

	private Task getTaskByCheckRequest(ITerminal terminal, PluginContainer pluginContainer) throws InterruptedException, BadLocationException {
		boolean displayTemp = display;
		String string;
		String[] splitResult;
		int position;
		Task temp = null;
		List<String> stringList = ((TaskOutputFormatter) formatter).formatOutput(list);
		display = false;
		terminal.update(pluginContainer);
		position = terminal.checkRequest(stringList);
		display = displayTemp;
		terminal.update(pluginContainer);
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
			if (task.category.equals(splitResult[0]) && task.name.equals(splitResult[1])) {
				temp = task;
			}
		}
		return temp;
	}
}