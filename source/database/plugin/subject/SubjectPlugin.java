package database.plugin.subject;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import org.w3c.dom.NamedNodeMap;
import database.plugin.Backup;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class SubjectPlugin extends InstancePlugin<Subject> {
	public SubjectPlugin(Storage storage, Backup backup) {
		super("subject", storage, new SubjectOutputFormatter(), backup);
	}

	@Command(tag = "add") public void addRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("add", getTagsAsRegex());
		map.put("score", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("maximum points", "[0-9]{1,13}(\\.[0-9]*)?");
		request(map);
		Subject toChange = getSubject(map.get("add"));
		backup.backupChangeBefor(toChange, this);
		toChange.setGrade(Double.parseDouble(map.get("score")), Double.parseDouble(map.get("maximum points")));
		backup.backupChangeAfter(toChange, this);
		update();
	}

	@Override public Subject create(Map<String, String> parameter) {
		return new Subject(	parameter.get("name"), parameter.get("tag"), Double.valueOf(parameter.get("score")), Double.valueOf(parameter.get("maxPoints")),
							Integer.valueOf(parameter.get("counter")));
	}

	@Override public Subject create(NamedNodeMap nodeMap) {
		return new Subject(	nodeMap.getNamedItem("name").getNodeValue(), nodeMap.getNamedItem("tag").getNodeValue(), Double.valueOf(nodeMap.getNamedItem("score").getNodeValue()),
							Double.valueOf(nodeMap.getNamedItem("maxPoints").getNodeValue()), Integer.valueOf(nodeMap.getNamedItem("counter").getNodeValue()));
	}

	@Command(tag = "new") public void createRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ].*");
		map.put("tag", "[a-zöäüß]*");
		request(map);
		map.put("maxPoints", "0");
		map.put("score", "0");
		map.put("counter", "0");
		createAndAdd(map);
		update();
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
