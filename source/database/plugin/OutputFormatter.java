package database.plugin;

public abstract interface OutputFormatter<T extends Instance> {
	abstract String getInitialOutput(Iterable<T> iterable);
}
