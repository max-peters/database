package database.plugin;

public abstract class Instance {
	public String			identity;
	protected InstanceList	list;

	public Instance(String identity, InstanceList list) {
		this.identity = identity;
		this.list = list;
	}

	@Override public abstract String toString();
}