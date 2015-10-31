package database.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import database.main.Terminal;
import database.main.date.Date;

public abstract class Plugin {
	protected String	identity;
	protected boolean	display;
	private boolean		changes;

	public Plugin(String identity) {
		this.identity = identity;
		this.changes = false;
	}

	@Command(tag = "display") public void display() throws InterruptedException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("display", "(true|false)");
		request(map);
		display = Boolean.valueOf(map.get("display"));
		update();
	}

	protected void request(Map<String, String> map) throws InterruptedException {
		for (Entry<String, String> entry : map.entrySet()) {
			String parameterInformation = Terminal.request(entry.getKey(), entry.getValue());
			if (entry.getKey().equals("date")) {
				parameterInformation = new Date(parameterInformation).toString();
			}
			map.replace(entry.getKey(), parameterInformation);
		}
	}

	public void update() {
		Terminal.update();
		setChanges(true);
	}

	public String getCommandTags() {
		String regex = "(";
		ArrayList<String> strings = new ArrayList<String>();
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				strings.add(method.getAnnotation(Command.class).tag());
			}
		}
		for (String string : strings) {
			regex = regex + string;
			if (!(strings.indexOf(string) == strings.size() - 1)) {
				regex = regex + "|";
			}
		}
		return regex + ")";
	}

	public void conduct(String command) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				if (method.getAnnotation(Command.class).tag().equals(command)) {
					method.invoke(this, (Object[]) method.getParameterTypes());
					return;
				}
			}
		}
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public void setChanges(boolean changes) {
		this.changes = changes;
	}

	public boolean getChanges() {
		return changes;
	}

	public String getIdentity() {
		return identity;
	}

	public boolean getDisplay() {
		return display;
	}

	public String initialOutput() {
		return null;
	}

	public List<RequestInformation> getInformationList() {
		return null;
	}

	public void readInformation(RequestInformation pair) throws IOException {}
}