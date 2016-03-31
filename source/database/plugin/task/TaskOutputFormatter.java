package database.plugin.task;

import java.util.LinkedList;
import database.plugin.OutputFormatter;

public class TaskOutputFormatter extends OutputFormatter<Task> {
	@Override protected String getInitialOutput(LinkedList<Task> list) {
		String output = "";
		for (Task task : list) {
			output = output + "\u2610 " + task.name + System.getProperty("line.separator");
		}
		return output;
	}
}
