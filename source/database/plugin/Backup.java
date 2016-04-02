package database.plugin;

import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;

public class Backup {
	private InstancePlugin<?>	instancePlugin;
	private Map<String, String>	newState;
	private Map<String, String>	oldState;

	public void backupChangeAfter(Instance newState, InstancePlugin<?> instancePlugin) {
		if (this.instancePlugin != instancePlugin) {
			throw new RuntimeException();
		}
		this.newState = newState.getParameter();
	}

	public void backupChangeBefor(Instance oldState, InstancePlugin<?> instancePlugin) {
		this.instancePlugin = instancePlugin;
		this.oldState = oldState.getParameter();
	}

	public void backupCreation(Instance instance, InstancePlugin<?> instancePlugin) {
		this.instancePlugin = instancePlugin;
		newState = instance.getParameter();
		oldState = null;
	}

	public void backupRemoval(Instance instance, InstancePlugin<?> instancePlugin) {
		this.instancePlugin = instancePlugin;
		newState = null;
		oldState = instance.getParameter();
	}

	public void cancel() throws BadLocationException, InterruptedException {
		if (newState != null) {
			if (oldState != null) {
				Map<String, String> localOldState = oldState;
				Map<String, String> localNewState = newState;
				instancePlugin.remove(instancePlugin.create(newState));
				instancePlugin.createAndAdd(localOldState);
				oldState = localNewState;
				newState = localOldState;
			}
			else {
				instancePlugin.remove(instancePlugin.create(newState));
			}
		}
		else if (oldState != null) {
			instancePlugin.createAndAdd(oldState);
		}
		else {
			Terminal.printLine("no command to cancel", StringType.SOLUTION, StringFormat.STANDARD);
			Terminal.waitForInput();
		}
		instancePlugin.setChanges(!instancePlugin.getChanges());
		Terminal.update();
	}
}
