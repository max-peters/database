package database.plugin;

import java.util.ArrayList;

public abstract class InstanceList<T extends Instance> extends ArrayList<T> {
	public String initialOutput() {
		return "";
	}
}
