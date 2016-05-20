package database.plugin;

import org.w3c.dom.Element;
import com.google.gson.Gson;

public abstract class Instance {
	@Override public abstract boolean equals(Object object);

	public void insertParameterd(Element element) {
		element.setAttribute("p", new Gson().toJson(this));
	}

	@Override public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}