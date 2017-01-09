package database.services.stringUtility;

public class Builder {
	private String string = "";

	public void append(String s) {
		string += s;
	}

	public void newLine() {
		string += System.lineSeparator();
	}

	public String build() {
		return string;
	}

	public int getLongestLine() {
		int length = 0;
		String[] sr = string.split(System.lineSeparator());
		for (int i = 0; i < sr.length; i++) {
			if (sr[i].length() > length) {
				length = sr[i].length();
			}
		}
		return length;
	}
}
