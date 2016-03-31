package database.plugin.subject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.plugin.Command;
import database.plugin.InstancePlugin;
import database.plugin.storage.Storage;

public class SubjectPlugin extends InstancePlugin<Subject> {
	public SubjectPlugin(Storage storage) {
		super("subject", new SubjectList(), storage);
	}

	@Command(tag = "add") public void addRequest() throws InterruptedException, BadLocationException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("add", ((SubjectList) getInstanceList()).getTagsAsRegex());
		map.put("score", "[0-9]{1,13}(\\.[0-9]*)?");
		map.put("maximum points", "[0-9]{1,13}(\\.[0-9]*)?");
		request(map);
		Subject toChange = ((SubjectList) getInstanceList()).getSubject(map.get("add"));
		toChange.setGrade(Double.parseDouble(map.get("score")), Double.parseDouble(map.get("maximum points")));
		toChange.calcPercent();
		update();
	}

	@Override public Subject create(Map<String, String> map) {
		return new Subject(map);
	}

	@Command(tag = "new") public void createRequest()	throws InterruptedException, BadLocationException, IOException, InstantiationException, IllegalAccessException,
														IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("name", "[A-ZÖÄÜ].*");
		map.put("tag", "[a-zöäüß]*");
		request(map);
		map.put("maxPoints", "0");
		map.put("score", "0");
		map.put("counter", "0");
		instanceList.add(new Subject(map));
		update();
	}
}
