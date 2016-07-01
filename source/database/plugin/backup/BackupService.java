package database.plugin.backup;

import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.FormatterProvider;
import database.plugin.Instance;
import database.plugin.InstancePlugin;

public class BackupService {
	private LinkedList<Backup>	backups	= new LinkedList<Backup>();
	private boolean				changes;

	public void backupChangeAfter(Instance newState, InstancePlugin<?> instancePlugin) {
		if (backups.size() != 1) {
			throw new IllegalStateException();
		}
		backups.get(0).backupChangeAfter(newState, instancePlugin);
	}

	public void backupChangeBefor(Instance oldState, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		clear();
		b.backupChangeBefor(oldState, instancePlugin);
		backups.add(b);
	}

	public void backupCreation(Instance instance, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		clear();
		b.backupCreation(instance, instancePlugin);
		backups.add(b);
	}

	public void backupRelatedCreation(Instance instance, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		b.backupCreation(instance, instancePlugin);
		backups.add(b);
	}

	public void backupRemoval(Instance instance, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		clear();
		b.backupRemoval(instance, instancePlugin);
		backups.add(b);
	}

	public boolean isChanged() {
		for (Backup backup : backups) {
			if (backup.isChanged()) {
				return true;
			}
		}
		return changes;
	}

	public void restore(Terminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException, InterruptedException {
		if (backups.size() == 0) {
			terminal.printLine("no command to cancel", StringType.SOLUTION, StringFormat.STANDARD);
			terminal.waitForInput();
		}
		for (Backup backup : backups) {
			backup.restore(terminal, pluginContainer, formatterProvider);
		}
	}

	public void save() {
		backups.clear();
		changes = false;
	}

	private void clear() {
		for (Backup backup : backups) {
			if (backup.isChanged()) {
				changes = true;
				break;
			}
		}
		backups.clear();
	}
}
