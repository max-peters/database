package database.main;

import java.util.LinkedList;
import java.util.List;
import database.services.stringComplete.Tupel;

public class Logger {
	private long						beginTime;
	private List<Tupel<Long, String>>	list;
	private static Logger				instance;

	private Logger() {
		list = new LinkedList<>();
		beginTime = System.currentTimeMillis();
	}

	public void log(String string) {
		long time = System.currentTimeMillis() - beginTime;
		list.add(new Tupel<>(time, string));
		System.out.println(time + ": " + string);
	}

	public static Logger Instance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}
}
