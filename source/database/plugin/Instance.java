package database.plugin;

import java.util.Map;

public abstract class Instance implements Comparable<Instance> {
	private String				identity;
	private Map<String, String>	parameter;
	private InstanceList		instanceList;

	public Instance(Map<String, String> parameter, String identity, InstanceList list) {
		this.parameter = parameter;
		this.identity = identity;
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
		return identity;
	}

	public InstanceList getInstanceList() {
		return instanceList;
	}
}