package database.plugin;

import com.google.gson.Gson;

public abstract class Instance {
	@Override public abstract boolean equals(Object object);

	@Override public String toString() {
		return new Gson().toJson(this);
	}
}