package database.plugin;

import java.util.Map;

public abstract class Instance {
	private Map<String, String> parameter;

	public Instance(Map<String, String> parameter) {
		this.parameter = parameter;
	}

	public String getParameter(String key) {
		return parameter.get(key);
	}

	protected void setParameter(String key, String value) {
		parameter.replace(key, value);
	}

	public Map<String, String> getParameter() {
		return parameter;
	}

	public String getIdentity() {
		return toString();
	}
}