package database.plugin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;

import jdk.nashorn.internal.objects.annotations.Getter;

import org.eclipse.jgit.api.errors.GitAPIException;

import database.main.Administration;
import database.main.date.Date;

public abstract class Plugin {
	protected String			identity;
	protected Administration	administration;

	public Plugin(String identity, Administration administration) {
		this.identity = identity;
		this.administration = administration;
	}

	protected String[][] request(String[][] information) throws CancellationException, InterruptedException {
		String[][] parameter = new String[information.length][2];
		for (int i = 0; i < information.length; i++) {
			String parameterInformation = administration.request(information[i][0], information[i][1]);
			parameter[i][0] = information[i][0];
			if (information[i][0].equals("date")) {
				parameter[i][1] = new Date(parameterInformation).toString();
			}
			else {
				parameter[i][1] = parameterInformation;
			}
		}
		return parameter;
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

	public abstract void conduct(String command) throws InterruptedException, IOException, GitAPIException;
}
