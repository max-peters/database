package database.plugin;

import java.util.HashMap;
import java.util.Map;

public class FormatterProvider {
	private Map<Class<? extends Instance>, OutputFormatter<? extends Instance>> map = new HashMap<Class<? extends Instance>, OutputFormatter<? extends Instance>>();

	public OutputFormatter<? extends Instance> getFormatter(Class<? extends Instance> instanceClass) {
		return map.get(instanceClass);
	}

	public void register(OutputFormatter<? extends Instance> formatter, Class<? extends Instance> instanceClass) {
		map.put(instanceClass, formatter);
	}
}
