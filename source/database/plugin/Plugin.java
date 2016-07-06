package database.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import database.main.PluginContainer;
import database.main.WriterReader;
import database.main.userInterface.ITerminal;
import database.plugin.backup.BackupService;

public abstract class Plugin {
	public boolean	display;
	public String	identity;

	public Plugin(String identity) {
		this.identity = identity;
	}

	public void conduct(String command, ITerminal terminal, BackupService backupService, PluginContainer pluginContainer, WriterReader writerReader,
						FormatterProvider formatterProvider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method method : this.getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {
				if (method.getAnnotation(Command.class).tag().equals(command)) {
					List<Object> parameter = new ArrayList<Object>();
					for (Parameter p : method.getParameters()) {
						if (p.getType().equals(ITerminal.class)) {
							parameter.add(terminal);
						}
						else if (p.getType().equals(BackupService.class)) {
							parameter.add(backupService);
						}
						else if (p.getType().equals(PluginContainer.class)) {
							parameter.add(pluginContainer);
						}
						else if (p.getType().equals(WriterReader.class)) {
							parameter.add(writerReader);
						}
						else if (p.getType().equals(FormatterProvider.class)) {
							parameter.add(formatterProvider);
						}
						else {
							throw new InvalidParameterException();
						}
					}
					method.invoke(this, parameter.toArray());
					return;
				}
			}
		}
	}

	@Command(tag = "display") public void display(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider)	throws InterruptedException,
																																			BadLocationException {
		display = Boolean.valueOf(terminal.request("display", "(true|false)"));
		terminal.update(pluginContainer, formatterProvider);
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

	public abstract void initialOutput(ITerminal terminal, PluginContainer pluginContainer, FormatterProvider formatterProvider) throws BadLocationException;

	public abstract void print(Document document, Element appendTo);

	public abstract void read(Node node) throws ParserConfigurationException;
}