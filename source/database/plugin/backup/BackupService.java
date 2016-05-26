package database.plugin.backup;

import java.util.LinkedList;
import javax.swing.text.BadLocationException;
import database.main.userInterface.StringFormat;
import database.main.userInterface.StringType;
import database.main.userInterface.Terminal;
import database.plugin.Instance;
import database.plugin.InstancePlugin;

public class BackupService {
	private static LinkedList<Backup>	backups	= new LinkedList<Backup>();
	private static boolean				changes;

	public static void backupCreation(Instance instance, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		clear();
		b.backupCreation(instance, instancePlugin);
		backups.add(b);
	}

	public static void backupRelatedCreation(Instance instance, InstancePlugin<?> instancePlugin) {
		if (backups.size() == 0) {
			throw new IllegalStateException();
		}
		Backup b = new Backup();
		b.backupCreation(instance, instancePlugin);
		backups.add(b);
	}

	public static void backupRemoval(Instance instance, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		clear();
		b.backupRemoval(instance, instancePlugin);
		backups.add(b);
	}

	public static void backupChangeBefor(Instance oldState, InstancePlugin<?> instancePlugin) {
		Backup b = new Backup();
		clear();
		b.backupChangeBefor(oldState, instancePlugin);
		backups.add(b);
	}

	public static void backupChangeAfter(Instance newState, InstancePlugin<?> instancePlugin) {
		if (backups.size() != 1) {
			throw new IllegalStateException();
		}
		backups.get(0).backupChangeAfter(newState, instancePlugin);
	}

	public static void save() {
		backups.clear();
		changes = false;
	}

	public static void restore() throws BadLocationException, InterruptedException {
		if (backups.size() == 0) {
			Terminal.printLine("no command to cancel", StringType.SOLUTION, StringFormat.STANDARD);
			Terminal.waitForInput();
		}
		for (Backup backup : backups) {
			backup.restore();
		}
	}

	public static boolean isChanged() {
		for (Backup backup : backups) {
			if (backup.isChanged()) {
				return true;
			}
		}
		return changes;
	}

	private static void clear() {
		for (Backup backup : backups) {
			if (backup.isChanged()) {
				changes = true;
				break;
			}
		}
		backups.clear();
	}
}
