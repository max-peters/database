package database.plugin;

public abstract class OutputFormatter<T extends Instance> {
	protected abstract String getInitialOutput(Iterable<T> iterable);
}
