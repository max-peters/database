package database.plugin;

import com.google.gson.Gson;

public abstract class Instance {
	@Override public boolean equals(Object object) {
		Gson gson = new Gson();
		return gson.toJson(this).equals(gson.toJson(object));
	}

	@Override public String toString() {
		return new Gson().toJson(this);
	}
}