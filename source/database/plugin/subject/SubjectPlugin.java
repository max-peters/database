package database.plugin.subject;

import javax.swing.text.BadLocationException;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.Storage;
import database.plugin.backup.BackupService;

public class SubjectPlugin extends InstancePlugin<Subject> {
	public SubjectPlugin(Storage storage) {
		super("subject", storage, new SubjectOutputFormatter(), Subject.class);
	}

	@Command(tag = "add") public void addRequest() throws InterruptedException, BadLocationException {
		Subject toChange = getSubject(Terminal.request("add", getTagsAsRegex()));
		BackupService.backupChangeBefor(toChange, this);
		toChange.setGrade(	Double.parseDouble(Terminal.request("score", "[0-9]{1,13}(\\.[0-9]*)?")),
							Double.parseDouble(Terminal.request("maximum points", "[0-9]{1,13}(\\.[0-9]*)?")));
		BackupService.backupChangeAfter(toChange, this);
		Terminal.update();
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		Subject subject = new Subject(Terminal.request("name", "[A-ZÖÄÜ].*"), Terminal.request("tag", "[a-zöäüß]*"), 0.0, 0.0, 0);
		add(subject);
		BackupService.backupCreation(subject, this);
		Terminal.update();
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
