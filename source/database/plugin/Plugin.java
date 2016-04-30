package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import database.main.userInterface.Terminal;

public abstract class Plugin {
	protected Backup	backup;
	private boolean		display;
	private String		identity;

	public Plugin(String identity, Backup backup) {
		this.identity = identity;
		this.backup = backup;
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
		display = Boolean.valueOf(Terminal.request("display", "(true|false)"));
		Terminal.update();
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
		return regex.endsWith("|") ? regex.substring(0, regex.lastIndexOf("|")) + ")" : "()";
	}

	public boolean getDisplay() {
		return display;
	}

	public String getIdentity() {
		return identity;
	}

	public void initialOutput() throws BadLocationException {}

	public void print(Document document, Element element) {}

	public void read(String nodeName, NamedNodeMap nodeMap) throws ParserConfigurationException {}

	public void setDisplay(boolean display) {
		this.display = display;
	}
}