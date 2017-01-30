package database.main;

import java.util.LinkedList;
import java.util.List;
import database.services.stringComplete.Tupel;

public class Logger {
	private static Logger				instance;
	private List<Tupel<Long, String>>	list;
	private long						beginTime;

	private Logger() {
		this.list = new LinkedList<>();
		this.beginTime = System.currentTimeMillis();
	}

	public static Logger Instance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	public void log(String string) {
		long time = System.currentTimeMillis() - beginTime;
		list.add(new Tupel<>(time, string));
		System.out.println(time + ": " + string);
	}
}
