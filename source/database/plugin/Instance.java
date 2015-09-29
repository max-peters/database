package database.plugin;

import java.util.Map;

public abstract class Instance implements Comparable<Instance> {
	private Map<String, String>	parameter;
	private InstanceList		instanceList;

	public Instance(Map<String, String> parameter, InstanceList list) {
		this.parameter = parameter;
		this.instanceList = list;
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

	public int compareTo(Instance instance) {
		return 0;
	}

	public String getIdentity() {
		return toString();
	}

	public InstanceList getInstanceList() {
		return instanceList;
	}
}