package database.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.text.BadLocationException;
import database.main.date.Date;
import database.main.userInterface.Terminal;

public abstract class Plugin {
	private boolean	changes;
	private boolean	display;
	private String	identity;

	public Plugin(String identity) {
		this.identity = identity;
		changes = false;
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

	@Command(tag = "display") public void display() throws InterruptedException, BadLocationException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("display", "(true|false)");
		request(map);
		display = Boolean.valueOf(map.get("display"));
		update();
	}

	public boolean getChanges() {
		return changes;
	}

	public String getCommandTags(Class<?> classWithMethods) {
		String regex = "(";
		ArrayList<String> strings = new ArrayList<String>();
		for (Method method : classWithMethods.getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				strings.add(method.getAnnotation(Command.class).tag());
			}
		}
		for (String string : strings) {
			regex += string + "|";
		}
		return regex.substring(0, regex.lastIndexOf("|")) + ")";
	}

	public boolean getDisplay() {
		return display;
	}

	public String getIdentity() {
		return identity;
	}

	public void initialOutput() throws BadLocationException {}

	public List<PrintInformation> print() {
		return null;
	}

	public void read(PrintInformation pair)	throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
											NoSuchMethodException, SecurityException {}

	public void setChanges(boolean changes) {
		this.changes = changes;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public void update() throws BadLocationException {
		Terminal.update();
		setChanges(true);
	}

	protected void request(Map<String, String> map) throws InterruptedException, BadLocationException {
		for (Entry<String, String> entry : map.entrySet()) {
			String parameterInformation = Terminal.request(entry.getKey(), entry.getValue());
			if (entry.getKey().equals("date")) {
				parameterInformation = new Date(parameterInformation).toString();
			}
			map.replace(entry.getKey(), parameterInformation);
		}
	}
}