package database.plugin.subject;

import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class SubjectPlugin extends InstancePlugin<Subject> {
	public SubjectPlugin(Storage storage) {
		super("subject", storage, new SubjectOutputFormatter());
	}

	@Command(tag = "add") public void addRequest() throws InterruptedException, BadLocationException {
		Subject toChange = getSubject(Terminal.request("add", getTagsAsRegex()));
		toChange.setGrade(	Double.parseDouble(Terminal.request("score", "[0-9]{1,13}(\\.[0-9]*)?")),
							Double.parseDouble(Terminal.request("maximum points", "[0-9]{1,13}(\\.[0-9]*)?")));
		Terminal.update();
	}

	@Override public Subject create(NamedNodeMap nodeMap) {
		return new Subject(	nodeMap.getNamedItem("name").getNodeValue(), nodeMap.getNamedItem("tag").getNodeValue(), Double.valueOf(nodeMap.getNamedItem("score").getNodeValue()),
							Double.valueOf(nodeMap.getNamedItem("maxPoints").getNodeValue()), Integer.valueOf(nodeMap.getNamedItem("counter").getNodeValue()));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		add(new Subject(Terminal.request("name", "[A-ZÖÄÜ].*"), Terminal.request("tag", "[a-zöäüß]*"), 0.0, 0.0, 0));
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
