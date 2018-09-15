package database.services.stringUtility;

import java.util.LinkedList;
import java.util.List;

public class Builder {
	private LinkedList<String> list;

	public Builder() {
		list = new LinkedList<>();
		list.add("");
	}

	public void append(String s) {
		if (isEmpty()) {
			list.add(s);
		}
		else {
			list.addLast(list.removeLast() + s);
		}
	}

	public String build() {
		String s = "";
		if (!isEmpty()) {
			while (list.getLast().isEmpty()) {
				list.removeLast();
			}
		}
		for (int i = 0; i < list.size(); i++) {
			s += list.get(i);
			if (i < list.size() - 1) {
				s += System.lineSeparator();
			}
		}
		return s;
	}

	public int getLongestLine() {
		int length = 0;
		for (String string : list) {
			if (string.length() > length) {
				length = string.length();
			}
		}
		return length;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public void newLine() {
		list.add("");
	}

	public List<String> toStringList() {
		if (!isEmpty()) {
			while (list.getLast().isEmpty()) {
				list.removeLast();
			}
		}
		return list;
	}
}
