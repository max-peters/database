package database.plugin.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import database.plugin.OutputFormatter;

public class TaskOutputFormatter extends OutputFormatter<Task> {
	protected List<String> formatOutput(Iterable<Task> iterable) {
		List<String> output = new ArrayList<String>();
		List<String> categories = new ArrayList<String>();
		String blanks;
		int longestCategory = 0;
		for (Task task : iterable) {
			if (!categories.contains(task.category)) {
				categories.add(task.category);
			}
			if (task.category.length() > longestCategory) {
				longestCategory = task.category.length();
			}
		}
		Collections.sort(categories);
		for (String string : categories) {
			for (Task task : iterable) {
				if (task.category.equals(string)) {
					blanks = "";
					for (int i = task.category.length(); i <= longestCategory; i++) {
						blanks += " ";
					}
					output.add(task.category + blanks + "- " + task.name);
				}
			}
		}
		return output;
	}

	@Override protected String getInitialOutput(Iterable<Task> iterable) {
		String output = "";
		for (String string : formatOutput(iterable)) {
			output += " \u2610 " + string + System.getProperty("line.separator");
		}
		return output;
	}
}
