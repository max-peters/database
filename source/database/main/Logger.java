package database.main;

import java.util.LinkedList;
import java.util.List;

import database.main.userInterface.ITerminal;
import database.main.userInterface.StringFormat;
import database.services.ServiceRegistry;
import database.services.settings.ISettingsProvider;
import database.services.settings.InternalParameters;
import database.services.stringComplete.Tupel;
import database.services.stringUtility.StringUtility;

public class Logger {
	private long beginTime;
	private List<Tupel<Long, String>> list;
	private static Logger instance;

	private Logger() {
		list = new LinkedList<>();
		beginTime = System.currentTimeMillis();
	}

	public void log(String string) {
		long time = System.currentTimeMillis() - beginTime;
		list.add(new Tupel<>(time, string));
		System.out.println(time + ": " + string);
	}

	public void print() {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		InternalParameters internalParameters = ServiceRegistry.Instance().get(ISettingsProvider.class)
				.getInternalParameters();
		int timeLength = 0;
		StringUtility stringUtility = new StringUtility();
		if (!list.isEmpty()) {
			timeLength = list.get(list.size() - 1).first.toString().length();
		}
		if (internalParameters.getDisplayLogger()) {
			for (Tupel<Long, String> tupel : list) {
				terminal.collectLine(" " + stringUtility.preIncrementTo(tupel.first.toString(), timeLength, ' ') + ": "
						+ tupel.second, StringFormat.STANDARD, "logger");
			}
		}
	}

	public static Logger Instance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}
}
