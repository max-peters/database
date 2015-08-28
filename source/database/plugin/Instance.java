package database.plugin;

public abstract class Instance implements Comparable<Instance> {
	public String			identity;
	protected String[][]	parameter;
	protected InstanceList	list;

	public Instance(String[][] parameter, String identity, InstanceList list) {
		this.parameter = parameter;
		this.identity = identity;
		this.list = list;
	}

	public String getParameter(String tag) {
		for (int i = 0; i < parameter.length; i++) {
			if (parameter[i][0].equals(tag)) {
				return parameter[i][1];
			}
		}
		return null;
	}

	protected void setParameter(String tag, String value) {
		for (int i = 0; i < parameter.length; i++) {
			if (parameter[i][0].equals(tag)) {
				parameter[i][1] = value;
			}
		}
	}

	public String[][] getParameter() {
		return parameter;
	}

	public int compareTo(Instance instance) {
		return 0;
	}
}