package database.plugin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import jdk.nashorn.internal.objects.annotations.Getter;

public abstract class Plugin {
	protected String	identity;

	public Plugin(String identity) {
		this.identity = identity;
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

	@Getter public String getIdentity() {
		return identity;
	}

	public abstract void conduct(String command) throws InterruptedException, IOException;
}
