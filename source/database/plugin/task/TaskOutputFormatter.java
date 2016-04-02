package database.plugin.task;

import database.plugin.OutputFormatter;

public class TaskOutputFormatter extends OutputFormatter<Task> {
	@Override protected String getInitialOutput(Iterable<Task> iterable) {
		String output = "";
		for (Task task : iterable) {
			output = output + "\u2610 " + task.name + System.getProperty("line.separator");
		}
		return output;
	}
}
