package database.plugin.subject;

import javax.swing.text.BadLocationException;
import database.main.PluginContainer;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.FormatterProvider;
import database.plugin.InstancePlugin;
import database.plugin.backup.BackupService;

public class SubjectPlugin extends InstancePlugin<Subject> {
	public SubjectPlugin() {
		super("subject", Subject.class);
	}

	@Command(tag = "add") public void addRequest(	Terminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws InterruptedException, BadLocationException {
		Subject toChange = getSubject(terminal.request("add", getTagsAsRegex()));
		backupService.backupChangeBefor(toChange, this);
		toChange.setGrade(	Double.parseDouble(terminal.request("score", "[0-9]{1,13}(\\.[0-9]*)?")),
							Double.parseDouble(terminal.request("maximum points", "[0-9]{1,13}(\\.[0-9]*)?")));
		backupService.backupChangeAfter(toChange, this);
		terminal.update(pluginContainer, formatterProvider);
	}

	@Command(tag = "new") public void createRequest(Terminal terminal, BackupService backupService, PluginContainer pluginContainer,
													FormatterProvider formatterProvider) throws InterruptedException, BadLocationException {
		Subject subject = new Subject(terminal.request("name", "[A-ZÖÄÜ].*"), terminal.request("tag", "[a-zöäüß]*"), 0.0, 0.0, 0);
		add(subject);
		backupService.backupCreation(subject, this);
		terminal.update(pluginContainer, formatterProvider);
	}

	private Subject getSubject(String tag) {
		Subject wanted = null;
		for (Subject subject : getIterable()) {
			if (subject.tag.equals(tag)) {
				wanted = subject;
			}
		}
		return wanted;
	}

	private String getTagsAsRegex() {
		String regex = "(";
		for (Subject subject : getIterable()) {
			regex += subject.tag + "|";
		}
		return regex.substring(0, regex.lastIndexOf("|")) + ")";
	}
}
