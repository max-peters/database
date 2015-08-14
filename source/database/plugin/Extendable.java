package database.plugin;

import java.util.ArrayList;

public abstract interface Extendable {
	public abstract void initialise();

	public abstract ArrayList<InstancePlugin> getExtentions();
}
