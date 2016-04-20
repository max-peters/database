package database.plugin;

import java.util.Map;
import org.w3c.dom.Element;

public abstract class Instance {
	@Override public abstract boolean equals(Object object);

	public abstract Map<String, String> getParameter();

	public abstract void insertParameter(Element element);

	@Override public String toString() {
		return getParameter() + "";
	}
}