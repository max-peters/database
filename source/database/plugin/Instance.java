package database.plugin;

import org.w3c.dom.Element;

public abstract class Instance {
	@Override public abstract boolean equals(Object object);

	public abstract void insertParameter(Element element);
}