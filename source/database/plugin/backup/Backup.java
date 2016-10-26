package database.plugin.backup;

import javax.swing.text.BadLocationException;
import com.google.gson.Gson;
import database.main.PluginContainer;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.plugin.FormatterProvider;
import database.plugin.Instance;
import database.plugin.InstancePlugin;

public class Backup {
	private boolean								changes;
	private InstancePlugin<? extends Instance>	instancePlugin;
	private String								newState;
	private String								oldState;

	protected void backupChangeAfter(Instance newState, InstancePlugin<?> instancePlugin) {
		if (this.instancePlugin != instancePlugin) {
			throw new RuntimeException();
		}
		this.newState = newState.toString();
		changes = true;
	}

	protected void backupChangeBefor(Instance oldState, InstancePlugin<?> instancePlugin) {
		this.instancePlugin = instancePlugin;
		this.oldState = oldState.toString();
	}

	protected void backupCreation(Instance instance, InstancePlugin<?> instancePlugin) {
		this.instancePlugin = instancePlugin;
		newState = instance.toString();
		oldState = null;
		changes = true;
	}

	protected void backupRemoval(Instance instance, InstancePlugin<?> instancePlugin) {
		this.instancePlugin = instancePlugin;
		newState = null;
		oldState = instance.toString();
		changes = true;
	}

	protected boolean isChanged() {
		return changes;
	}

	protected void restore(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		Gson gson = new Gson();
		if (newState != null) {
			if (oldState != null) {
				String temp = oldState;
				instancePlugin.remove(gson.fromJson(newState, instancePlugin.instanceClass));
				instancePlugin.createAndAdd(oldState);
				oldState = newState;
				newState = temp;
				terminal.update(pluginContainer, formatterProvider);
				terminal.printLine("reverted change of " + formatJSON(oldState) + System.lineSeparator() + "to" + formatJSON(newState), StringType.SOLUTION, StringFormat.STANDARD);
				terminal.waitForInput();
			}
			else {
				instancePlugin.remove(gson.fromJson(newState, instancePlugin.instanceClass));
				oldState = newState;
				newState = null;
				terminal.update(pluginContainer, formatterProvider);
				terminal.printLine("removed " + formatJSON(oldState), StringType.SOLUTION, StringFormat.STANDARD);
				terminal.waitForInput();
			}
			changes = !isChanged();
		}
		else if (oldState != null) {
			instancePlugin.createAndAdd(oldState);
			newState = oldState;
			oldState = null;
			changes = !isChanged();
			terminal.update(pluginContainer, formatterProvider);
			terminal.printLine("added " + formatJSON(newState), StringType.SOLUTION, StringFormat.STANDARD);
			terminal.waitForInput();
		}
		else {
			terminal.printLine("no command to cancel", StringType.SOLUTION, StringFormat.STANDARD);
			terminal.waitForInput();
		}
	}

	private String formatJSON(String json) {
		return System.lineSeparator() + json.replace(",", "," + System.lineSeparator()).replace("{", "").replace("}", "");
	}
}
