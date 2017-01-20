package database.services.undoRedo;

import java.util.LinkedList;

public class BoundedStack<T> {
	private int				limit;
	private LinkedList<T>	list;

	public BoundedStack(int maxSize) {
		limit = maxSize;
		list = new LinkedList<>();
	}

	public void clear() {
		list.clear();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public T peek() {
		if (list.size() > 0) {
			return list.getFirst();
		}
		return null;
	}

	public T pop() {
		if (list.size() > 0) {
			T value = list.getFirst();
			list.removeFirst();
			return value;
		}
		return null;
	}

	public void push(T value) {
		if (list.size() >= limit) {
			list.removeLast();
		}
		list.addFirst(value);
	}
}