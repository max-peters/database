package database.plugin;

import java.util.Map;

public abstract class Instance {
	@Override public abstract boolean equals(Object object);

	public abstract Map<String, String> getParameter();
}