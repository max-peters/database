package database.plugin;

import java.util.LinkedList;

public abstract class OutputFormatter<T extends Instance> {
	protected abstract String getInitialOutput(LinkedList<T> list);
}
