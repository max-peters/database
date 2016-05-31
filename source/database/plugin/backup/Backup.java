package database.plugin.backup;

import javax.swing.text.BadLocationException;
import com.google.gson.Gson;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Instance;
import database.plugin.InstancePlugin;

public class Backup {
	private InstancePlugin<? extends Instance>	instancePlugin;
	private String								newState;
	private String								oldState;
	private boolean								changes;

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

	protected void restore() throws BadLocationException, InterruptedException {
		Gson gson = new Gson();
		if (newState != null) {
			if (oldState != null) {
				String temp = oldState;
				instancePlugin.remove(gson.fromJson(newState, instancePlugin.getInstanceClass()));
				instancePlugin.createAndAdd(oldState);
				oldState = newState;
				newState = temp;
			}
			else {
				instancePlugin.remove(gson.fromJson(newState, instancePlugin.getInstanceClass()));
				oldState = newState;
				newState = null;
			}
			changes = !isChanged();
		}
		else if (oldState != null) {
			instancePlugin.createAndAdd(oldState);
			newState = oldState;
			oldState = null;
			changes = !isChanged();
		}
		else {
			Terminal.printLine("no command to cancel", StringType.SOLUTION, StringFormat.STANDARD);
			Terminal.waitForInput();
		}
		Terminal.update();
	}

	protected boolean isChanged() {
		return changes;
	}
}