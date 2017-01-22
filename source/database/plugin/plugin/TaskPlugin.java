package database.plugin.plugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.plugin.connector.TaskDatabaseConnector;
import database.plugin.element.Task;
import database.plugin.outputHandler.TaskOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.IConnectorRegistry;
import database.services.undoRedo.CommandHandler;
import database.services.undoRedo.command.ChangeCommand;
import database.services.undoRedo.command.DeleteCommand;
import database.services.undoRedo.command.InsertCommand;

public class TaskPlugin extends Plugin {
	public TaskPlugin() {
		super("task", new TaskOutputHandler());
	}

	@Command(tag = "edit") public void changeRequest() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		Task task = getTaskByCheckRequest();
		if (task != null) {
			String newName = terminal.request("new name", RequestType.NAME_NUMBER, task.name);
			CommandHandler.Instance().executeCommand(new ChangeCommand(task, new Task(newName)));
		}
	}

	@Command(tag = "check") public void checkRequest() throws InterruptedException, BadLocationException, IOException, SQLException {
		Task task = getTaskByCheckRequest();
		if (task != null) {
			CommandHandler.Instance().executeCommand(new DeleteCommand(task));
		}
	}

	@Command(tag = "new") public void createRequest() throws BadLocationException, InterruptedException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String name = terminal.request("name", RequestType.NAME_NUMBER);
		Task task = new Task(name);
		CommandHandler.Instance().executeCommand(new InsertCommand(task));
	}

	private Task getTaskByCheckRequest() throws InterruptedException, BadLocationException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		IConnectorRegistry registry = ServiceRegistry.Instance().get(IConnectorRegistry.class);
		TaskDatabaseConnector taskConnector = (TaskDatabaseConnector) registry.get(Task.class);
		boolean displayTemp = display;
		int position;
		List<String> stringList = new LinkedList<>();
		List<Task> taskList = taskConnector.getList();
		display = false;
		terminal.update();
		for (Task task : taskList) {
			stringList.add(task.name);
		}
		position = terminal.checkRequest(stringList, "choose task");
		display = displayTemp;
		terminal.update();
		if (position != -1) {
			return taskList.get(position);
		}
		else {
			return null;
		}
	}
}